package com.github.igorperikov.heimdallr;

import com.github.igorperikov.heimdallr.epidemics.RandomNodeAntiEntropyMechanism;
import com.github.igorperikov.heimdallr.generated.ClusterStateTO;
import com.github.igorperikov.heimdallr.generated.NodeDefinitionTO;
import com.github.igorperikov.heimdallr.generated.Type;
import com.github.igorperikov.heimdallr.init.ServerBootstrapHelper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

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
        log.info("My name is {}", label);
    }

    public HeimdallrNode(int port, String peerAddress, int peerPort) {
        this(port);
        this.peerNodeAddress = new InetSocketAddress(peerAddress, peerPort);
    }

    public void start() {
        ScheduledFuture<?> infoPrintingFuture = null;
        ScheduledFuture<?> antiEntropyFuture = null;
        EventLoopGroup parentEventLoopGroup = new NioEventLoopGroup(1);
        EventLoopGroup childEventLoopGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = ServerBootstrapHelper.build(parentEventLoopGroup, childEventLoopGroup, port, this);
            Channel serverChannel = b.bind().sync().channel();
            log.info("Current node started listening on port {}", port);
            initOwnClusterState();
            if (peerNodeAddress != null) {
                new InterNodeMessageSender(this, childEventLoopGroup, peerNodeAddress).send();
            }
            infoPrintingFuture = new ClusterInfoLogger(this, childEventLoopGroup).startPrintingClusterInfo();
            antiEntropyFuture = new RandomNodeAntiEntropyMechanism(this, childEventLoopGroup).launch();
            serverChannel.closeFuture().sync();
            log.info("Shutting down node {}", label);
        } catch (InterruptedException e) {
            log.error("Interrupted", e);
        } finally {
            releaseResources(infoPrintingFuture, antiEntropyFuture, parentEventLoopGroup, childEventLoopGroup);
        }
    }

    private void initOwnClusterState() {
        clusterState = ClusterStateTO.newBuilder().putNodes(label.toString(), getNodeDefinition()).build();
    }

    private void releaseResources(
            ScheduledFuture<?> infoPrintingFuture,
            ScheduledFuture<?> antiEntropyFuture,
            EventLoopGroup parentEventLoopGroup,
            EventLoopGroup childEventLoopGroup
    ) {
        if (infoPrintingFuture != null) {
            infoPrintingFuture.cancel(true);
        }
        if (antiEntropyFuture != null) {
            antiEntropyFuture.cancel(true);
        }
        parentEventLoopGroup.shutdownGracefully().syncUninterruptibly();
        childEventLoopGroup.shutdownGracefully().syncUninterruptibly();
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
