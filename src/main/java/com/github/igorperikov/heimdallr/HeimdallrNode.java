package com.github.igorperikov.heimdallr;

import com.github.igorperikov.heimdallr.init.ClientBootstrapHelper;
import com.github.igorperikov.heimdallr.init.ServerBootstrapHelper;
import com.github.igorperikov.heimdallr.init.pojo.ClusterStateRequest;
import com.github.igorperikov.heimdallr.init.pojo.NodeDefinition;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.ScheduledFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class HeimdallrNode {
    private static final Logger log = LoggerFactory.getLogger(HeimdallrNode.class);

    private final int port;
    private final UUID name;
    private InetSocketAddress peerNodeAddress;
    private final Set<NodeDefinition> clusterNodes = new HashSet<>();

    public HeimdallrNode(int port) {
        this.port = port;
        this.name = UUID.randomUUID();
    }

    public HeimdallrNode(int port, String peerAddress, int peerPort) {
        this(port);
        this.peerNodeAddress = new InetSocketAddress(peerAddress, peerPort);
    }

    public void start() {
        ScheduledFuture<?> infoPrintingFuture = null;
        EventLoopGroup parentEventLoopGroup = new NioEventLoopGroup(1);
        EventLoopGroup childEventLoopGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = ServerBootstrapHelper.build(parentEventLoopGroup, childEventLoopGroup, port, this);
            ChannelFuture sync = b.bind().sync();
            log.info("{} node started listening on port {}", name, port);
            Channel serverChannel = sync.channel();
            if (peerNodeAddress != null) {
                ChannelFuture channelFuture = establishConnectToPeerNode(peerNodeAddress, childEventLoopGroup);
                log.info("Sending request to peer node");
                channelFuture.sync().channel().writeAndFlush(new ClusterStateRequest(getDefinition())).sync();
            } else {
                clusterNodes.add(new NodeDefinition(name, getNodeAddress()));
            }
            infoPrintingFuture = new ClusterInfoLogger().startPrintingCLusterInfo(serverChannel.eventLoop(), this);
            serverChannel.closeFuture().sync();
            log.info("Shutting down node {}", name);
        } catch (InterruptedException e) {
            log.error("Interrupted", e);
        } finally {
            if (infoPrintingFuture != null) {
                infoPrintingFuture.cancel(true);
            }
            parentEventLoopGroup.shutdownGracefully().syncUninterruptibly();
            childEventLoopGroup.shutdownGracefully().syncUninterruptibly();
        }
    }

    public ChannelFuture establishConnectToPeerNode(InetSocketAddress peerNodeAddress, EventLoopGroup eventLoopGroup) {
        Bootstrap bootstrap = ClientBootstrapHelper.build(eventLoopGroup, peerNodeAddress, this);
        return bootstrap.connect();
    }

    public NodeDefinition getDefinition() {
        return new NodeDefinition(name, getNodeAddress());
    }

    public Set<NodeDefinition> getClusterNodes() {
        return clusterNodes;
    }

    public void replaceClusterNodes(Collection<NodeDefinition> nodes) {
        clusterNodes.clear();
        clusterNodes.addAll(nodes);
    }

    public void addClusterNode(NodeDefinition nodeDefinition) {
        clusterNodes.add(nodeDefinition);
    }

    public InetSocketAddress getNodeAddress() {
        // TODO: localhost -> ip detection
        return new InetSocketAddress("localhost", port);
    }
}
