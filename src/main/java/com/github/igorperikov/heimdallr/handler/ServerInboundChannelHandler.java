package com.github.igorperikov.heimdallr.handler;

import com.github.igorperikov.heimdallr.ClusterDiffCalculator;
import com.github.igorperikov.heimdallr.ClusterStateMerger;
import com.github.igorperikov.heimdallr.HeimdallrNode;
import com.github.igorperikov.heimdallr.generated.ClusterStateDiffTO;
import com.github.igorperikov.heimdallr.generated.ClusterStateTO;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class ServerInboundChannelHandler extends SimpleChannelInboundHandler<ClusterStateTO> {
    private final HeimdallrNode node;
    private final ClusterStateMerger merger = new ClusterStateMerger();
    private final ClusterDiffCalculator resolver = new ClusterDiffCalculator();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClusterStateTO msg) throws Exception {
        log.info("Other node sent her cluster state and asking for a merged state");
        ClusterStateDiffTO diff = resolver.calculate(node.getClusterState(), msg);
        node.setClusterState(merger.merge(node.getClusterState(), diff));
        ctx.writeAndFlush(diff);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("Channel became inactive");
    }
}
