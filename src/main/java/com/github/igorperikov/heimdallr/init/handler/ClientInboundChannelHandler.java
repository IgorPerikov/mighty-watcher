package com.github.igorperikov.heimdallr.init.handler;

import com.github.igorperikov.heimdallr.HeimdallrNode;
import com.github.igorperikov.heimdallr.init.pojo.ClusterState;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientInboundChannelHandler extends SimpleChannelInboundHandler<ClusterState> {
    private static final Logger log = LoggerFactory.getLogger(ClientInboundChannelHandler.class);

    private final HeimdallrNode node;

    public ClientInboundChannelHandler(HeimdallrNode node) {
        this.node = node;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClusterState msg) throws Exception {
        log.info("Response from server acquired");
        node.replaceClusterNodes(msg.getNodes());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("Read completed, closing channel");
        ctx.close();
    }
}
