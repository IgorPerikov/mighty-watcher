package com.github.igorperikov.heimdallr.handler;

import com.github.igorperikov.heimdallr.HeimdallrNode;
import com.github.igorperikov.heimdallr.generated.ClusterStateTO;
import com.github.igorperikov.heimdallr.generated.NodeDefinitionTO;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class ServerInboundChannelHandler extends SimpleChannelInboundHandler<ClusterStateTO> {
    private final HeimdallrNode node;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClusterStateTO msg) throws Exception {
        log.info("Other node sent her cluster state and asking for a merged state");
        Map<String, NodeDefinitionTO> map = new HashMap<>(node.getClusterState().getNodesMap());
        for (NodeDefinitionTO def : msg.getNodesMap().values()) {
            String label = def.getLabel();
            if (map.containsKey(label)) {
                // resolve
                Instant incomingStateTime = Instant.parse(def.getTimestamp());
                Instant thisStateTime = Instant.parse(map.get(label).getTimestamp());
                if (incomingStateTime.isAfter(thisStateTime)) {
                    map.replace(label, def);
                }
            } else {
                map.put(label, def);
            }
        }

        ClusterStateTO build = ClusterStateTO.newBuilder().putAllNodes(map).build();
        node.setClusterState(build);
        ctx.writeAndFlush(build);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("Channel became inactive");
    }
}
