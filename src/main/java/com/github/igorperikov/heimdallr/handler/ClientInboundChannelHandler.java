package com.github.igorperikov.heimdallr.handler;

import com.github.igorperikov.heimdallr.HeimdallrNode;
import com.github.igorperikov.heimdallr.generated.ClusterState;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class ClientInboundChannelHandler extends SimpleChannelInboundHandler<ClusterState> {
    private final HeimdallrNode node;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClusterState msg) throws Exception {
        log.info("Response from server acquired");
        node.replaceClusterNodes(msg.getNodesMap().values());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("Read completed, closing channel");
        ctx.close();
    }
}
