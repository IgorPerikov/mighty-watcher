package com.github.igorperikov.heimdallr.init.handler;

import com.github.igorperikov.heimdallr.HeimdallrNode;
import com.github.igorperikov.heimdallr.init.pojo.ClusterState;
import com.github.igorperikov.heimdallr.init.pojo.ClusterStateRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AllArgsConstructor
public class ServerInboundChannelHandler extends SimpleChannelInboundHandler<ClusterStateRequest> {
    private static final Logger log = LoggerFactory.getLogger(ServerInboundChannelHandler.class);

    private final HeimdallrNode node;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClusterStateRequest msg) throws Exception {
        log.info("Request from client acquired");
        node.addClusterNode(msg.getNodeDefinition());
        ctx.writeAndFlush(new ClusterState(node.getClusterNodes()));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("Channel became inactive");
    }
}
