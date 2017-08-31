package com.github.igorperikov.heimdallr.handler;

import com.github.igorperikov.heimdallr.ClusterStateResolver;
import com.github.igorperikov.heimdallr.HeimdallrNode;
import com.github.igorperikov.heimdallr.generated.ClusterStateTO;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class ServerInboundChannelHandler extends SimpleChannelInboundHandler<ClusterStateTO> {
    private final HeimdallrNode node;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClusterStateTO msg) throws Exception {
        log.info("Other node sent her cluster state and asking for a merged state");
        ClusterStateTO resolvedState = ClusterStateResolver.resolve(node.getClusterState(), msg);
        node.setClusterState(resolvedState);
        ctx.writeAndFlush(resolvedState);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("Channel became inactive");
    }
}
