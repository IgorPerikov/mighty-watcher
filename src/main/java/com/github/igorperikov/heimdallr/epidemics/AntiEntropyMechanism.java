package com.github.igorperikov.heimdallr.epidemics;

import com.github.igorperikov.heimdallr.HeimdallrNode;
import com.github.igorperikov.heimdallr.MessageSender;
import com.github.igorperikov.heimdallr.generated.ClusterStateTO;
import com.github.igorperikov.heimdallr.generated.NodeDefinitionTO;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class AntiEntropyMechanism {
    private static final Random RANDOM = new Random();

    private final HeimdallrNode mainNode;
    private final EventLoopGroup eventLoop;

    public ScheduledFuture<?> launch() {
        return eventLoop.scheduleWithFixedDelay(() -> {
                    ClusterStateTO clusterState = mainNode.getClusterState();
                    List<NodeDefinitionTO> nodes = clusterState.getNodesMap().entrySet().stream()
                            .filter(entry -> !entry.getKey().equals(mainNode.getLabel().toString()))
                            .map(Map.Entry::getValue)
                            .collect(Collectors.toList());
                    send(nodes.get(RANDOM.nextInt(nodes.size())));
                },
                5,
                5,
                TimeUnit.SECONDS);
    }

    private void send(NodeDefinitionTO node) {
        Integer port = Integer.valueOf(node.getAddress().split(":")[1]);
        InetSocketAddress address = new InetSocketAddress("localhost", port);
        try {
            new MessageSender(mainNode, eventLoop, address).send();
        } catch (InterruptedException ignored) {}
    }
}
