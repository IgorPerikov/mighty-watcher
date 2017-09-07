package com.github.igorperikov.heimdallr.handler;

import com.github.igorperikov.heimdallr.ClusterStateMerger;
import com.github.igorperikov.heimdallr.HeimdallrNode;
import com.github.igorperikov.heimdallr.generated.ClusterStateDiffTO;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class ClientInboundChannelHandler extends SimpleChannelInboundHandler<ClusterStateDiffTO> {
    private final HeimdallrNode node;
    private final ClusterStateMerger merger = new ClusterStateMerger();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClusterStateDiffTO msg) throws Exception {
        log.info("Peer node answered her cluster state");
        node.setClusterState(merger.merge(node.getClusterState(), msg));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("Read completed, closing channel");
        ctx.close();
    }
}
