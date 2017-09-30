package com.github.igorperikov.heimdallr;

import com.github.igorperikov.heimdallr.domain.NodeDefinition;
import com.github.igorperikov.heimdallr.grpc.client.HeartbeatServiceClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Slf4j
public class HeartbeatMechanism {
    private final int heartbeatDelayInSeconds;
    private final HeimdallrNode node;
    private final HeartbeatServiceClient client = new HeartbeatServiceClient();

    public ScheduledFuture launch() {
        return Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(
                () -> {
                    List<NodeDefinition> otherNodes = node.getOtherLiveNodeDefinitions();
                    for (NodeDefinition nodeDefinition : otherNodes) {
                        Integer port = Integer.valueOf(nodeDefinition.getAddress().split(":")[1]);
                        String address = "localhost";
                        log.info("Sending heartbeat request to {}:{}", address, port);
                        try {
                            client.call(address, port);
                            log.info("Heartbeat to {}:{} was successful", address, port);
                        } catch (Exception e) {
                            log.warn("Heartbeat to {}:{} failed", address, port);
                            node.getClusterState().markWithTombstone(nodeDefinition.getLabel());
                        }
                    }
                },
                heartbeatDelayInSeconds,
                heartbeatDelayInSeconds,
                TimeUnit.SECONDS
        );
    }
}
