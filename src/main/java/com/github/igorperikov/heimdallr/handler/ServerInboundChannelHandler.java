package com.github.igorperikov.heimdallr.handler;

import com.github.igorperikov.heimdallr.HeimdallrNode;
import com.github.igorperikov.heimdallr.generated.ClusterState;
import com.github.igorperikov.heimdallr.generated.ClusterStateRequest;
import com.github.igorperikov.heimdallr.generated.NodeDefinition;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class ServerInboundChannelHandler extends SimpleChannelInboundHandler<ClusterStateRequest> {
    private final HeimdallrNode node;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClusterStateRequest msg) throws Exception {
        log.info("Request from client acquired");
        node.addClusterNode(msg.getNode());
        ClusterState clusterState = ClusterState.newBuilder()
                .putAllNodes(node.getClusterNodes()
                        .stream()
                        .collect(Collectors.toMap(NodeDefinition::getLabel, Function.identity())))
                .build();
        ctx.writeAndFlush(clusterState);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("Channel became inactive");
    }
}
