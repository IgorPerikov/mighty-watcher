package com.github.igorperikov.heimdallr.handler;

import com.github.igorperikov.heimdallr.HeimdallrNode;
import com.github.igorperikov.heimdallr.generated.ClusterState;
import com.github.igorperikov.heimdallr.generated.ClusterStateRequest;
import com.github.igorperikov.heimdallr.generated.NodeDefinition;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class ServerInboundChannelHandler extends SimpleChannelInboundHandler<ClusterStateRequest> {
    private final HeimdallrNode node;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClusterStateRequest msg) throws Exception {
        log.info("Request from client acquired");
        node.addClusterNode(msg.getNode());
        Map<String, NodeDefinition> map = new HashMap<>();
        for (NodeDefinition nodeDefinition : node.getClusterNodes()) {
            map.put(nodeDefinition.getLabel(), nodeDefinition);
        }
        ClusterState clusterState = ClusterState.newBuilder().putAllNodes(map).build();
        ctx.writeAndFlush(clusterState);
//        ctx.writeAndFlush(new ClusterState(node.getClusterNodes()
//                .stream()
//                .collect(Collectors.toMap(NodeDefinition::getLabel, o -> new NodeDefinition(o.getLabel(), o.getAddress())))
//        ));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("Channel became inactive");
    }
}
