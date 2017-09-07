package com.github.igorperikov.heimdallr;

import com.github.igorperikov.heimdallr.generated.NodeDefinitionTO;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class ClusterInfoLogger {
    private HeimdallrNode node;
    private EventLoopGroup eventLoop;

    public ScheduledFuture<?> startPrintingClusterInfo() {
        return eventLoop.scheduleWithFixedDelay(
                () -> {
                    String clusterStateString = node.getClusterState()
                            .getNodesMap()
                            .values()
                            .stream()
                            .map(NodeDefinitionTO::toString)
                            .collect(Collectors.joining("\r\n"));
                    log.info("Cluster state: \r\n[{}]", clusterStateString);
                },
                5,
                20,
                TimeUnit.SECONDS
        );
    }
}
