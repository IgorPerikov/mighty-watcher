package com.github.igorperikov.heimdallr;

import com.github.igorperikov.heimdallr.generated.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.UUID;

@Slf4j
public class HeimdallrNode {
    private final int port;

    @Getter
    private final UUID label;

    @Getter
    @Setter
    private ClusterStateTO clusterState;

    private InetSocketAddress peerNodeAddress;

    public HeimdallrNode(int port) {
        this.port = port;
        this.label = UUID.randomUUID();
    }

    public HeimdallrNode(int port, String peerAddress, int peerPort) {
        this(port);
        this.peerNodeAddress = new InetSocketAddress(peerAddress, peerPort);
    }

    public void start() {
        initOwnClusterState();

        Server server = ServerBuilder.forPort(port)
                .addService(new HeimdallrServiceImplementation(this))
                .build();
        log.info("{} start and listening on {}", label, port);

        if (peerNodeAddress != null) {
            ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder
                    .forAddress(peerNodeAddress.getHostString(), peerNodeAddress.getPort())
                    .usePlaintext(true);
            ManagedChannel channel = channelBuilder.build();
            HeimdallrServiceGrpc.HeimdallrServiceBlockingStub call = HeimdallrServiceGrpc.newBlockingStub(channel);
            ClusterStateDiffTO diff = call.getDiffWithOtherNodesState(clusterState);
            clusterState = new ClusterStateMerger().merge(clusterState, diff);
        }

        try {
            server.start();
        } catch (IOException e) {
            log.error("", e);
        }

        new ClusterInfoLogger(this).startPrintingClusterInfo();

        try {
            server.awaitTermination();
        } catch (InterruptedException e) {
            log.error("", e);
        }
    }

    private void initOwnClusterState() {
        clusterState = ClusterStateTO.newBuilder().putNodes(label.toString(), getNodeDefinition()).build();
    }

    public NodeDefinitionTO getNodeDefinition() {
        return NodeDefinitionTO.newBuilder()
                .setTimestamp(Instant.now().toString())
                .setType(Type.LIVE)
                .setLabel(label.toString())
                .setAddress(getNodeAddress().toString())
                .build();
    }

    public InetSocketAddress getNodeAddress() {
        // TODO: localhost -> ip detection
        return new InetSocketAddress("localhost", port);
    }
}
