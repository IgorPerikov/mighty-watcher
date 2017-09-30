package com.github.igorperikov.heimdallr;

import com.github.igorperikov.heimdallr.domain.ClusterState;
import com.github.igorperikov.heimdallr.domain.ClusterStateDiff;
import com.github.igorperikov.heimdallr.domain.NodeDefinition;
import com.github.igorperikov.heimdallr.epidemics.RandomNodeAntiEntropyMechanism;
import com.github.igorperikov.heimdallr.generated.Type;
import com.github.igorperikov.heimdallr.grpc.client.HeimdallrServiceClient;
import com.github.igorperikov.heimdallr.grpc.server.HeartbeatServiceImplementation;
import com.github.igorperikov.heimdallr.grpc.server.HeimdallrServiceImplementation;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

@Slf4j
public class HeimdallrNode {
    private final int port;

    @Getter
    private final UUID label = UUID.randomUUID();

    @Getter
    private ClusterState clusterState;

    private String peerNodeAddress;
    private Integer peerNodePort;

    public HeimdallrNode(int port) {
        this.port = port;
    }

    public HeimdallrNode(int port, String peerAddress, int peerPort) {
        this(port);
        this.peerNodeAddress = peerAddress;
        this.peerNodePort = peerPort;
    }

    public void start() {
        setupPersonalClusterState();

        Server server = ServerBuilder.forPort(port)
                .addService(new HeimdallrServiceImplementation(this))
                .addService(new HeartbeatServiceImplementation())
                .build();

        if (peerNodeAddress != null && peerNodePort != null) {
            log.info("Peer node {}:{} is provided", peerNodeAddress, peerNodePort);
            ClusterStateDiff diff = new HeimdallrServiceClient(peerNodeAddress, peerNodePort)
                    .getClusterStateDiff(getClusterState());
            clusterState.applyDiff(diff);
        }

        try {
            server.start();
            log.info("{} start and listening on {}", label, port);
        } catch (IOException e) {
            log.error("", e);
            System.exit(1);
        }

        ScheduledFuture antiEntropyFuture = new RandomNodeAntiEntropyMechanism(this).launch();
        ScheduledFuture clusterInfoFuture = new ClusterInfoLogger(this).launch();
        ScheduledFuture heartbeatFuture = new HeartbeatMechanism(this).launch();

        try {
            server.awaitTermination();
            antiEntropyFuture.cancel(true);
            clusterInfoFuture.cancel(true);
            heartbeatFuture.cancel(true);
        } catch (InterruptedException e) {
            log.error("", e);
        }
    }

    private void setupPersonalClusterState() {
        clusterState = new ClusterState(label.toString(), getNodeAddress());
    }

    public String getNodeAddress() {
        // TODO: localhost -> ip detection
        return "localhost:" + port;
    }

    public List<NodeDefinition> getOtherLiveNodeDefinitions() {
        return clusterState.getNodes().values().stream()
                .filter(node -> node.getType() == Type.LIVE)
                .filter(node -> !node.getLabel().equals(label.toString()))
                .collect(Collectors.toList());
    }
}
