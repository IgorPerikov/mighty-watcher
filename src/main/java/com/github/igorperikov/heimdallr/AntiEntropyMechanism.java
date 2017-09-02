package com.github.igorperikov.heimdallr;

import com.github.igorperikov.heimdallr.generated.ClusterStateTO;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@AllArgsConstructor
public class AntiEntropyMechanism {
    private final HeimdallrNode mainNode;
    private final EventLoopGroup eventLoop;

    public ScheduledFuture<?> launch() {
        return eventLoop.scheduleWithFixedDelay(() -> {
                    ClusterStateTO clusterState = mainNode.getClusterState();
                    clusterState.getNodesMap().entrySet().stream()
                            .filter(entry -> !entry.getKey().equals(mainNode.getLabel().toString()))
                            .map(Map.Entry::getValue)
                            .forEach(node -> {
                                // TODO:
                                Integer port = Integer.valueOf(node.getAddress().split(":")[1]);
                                InetSocketAddress address = new InetSocketAddress("localhost", port);
                                try {
                                    new MessageSender(mainNode, eventLoop, address).send();
                                } catch (InterruptedException ignored) {}
                            });
                },
                5,
                5,
                TimeUnit.SECONDS);
    }
}
