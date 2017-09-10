package com.github.igorperikov.heimdallr.epidemics;

import com.github.igorperikov.heimdallr.HeimdallrNode;
import com.github.igorperikov.heimdallr.InterNodeCommunicator;
import com.github.igorperikov.heimdallr.domain.ClusterState;
import com.github.igorperikov.heimdallr.domain.ClusterStateDiff;
import com.github.igorperikov.heimdallr.domain.NodeDefinition;
import com.github.igorperikov.heimdallr.exception.NoOtherNodesInClusterException;
import com.github.igorperikov.heimdallr.generated.Type;
import io.grpc.StatusRuntimeException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
public abstract class AntiEntropyMechanism {
    private final HeimdallrNode currentNode;

    public ScheduledFuture launch() {
        return Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
                    ClusterState clusterState = currentNode.getClusterState();
                    List<NodeDefinition> nodes = clusterState.getNodes().values().stream()
                            .filter(node -> node.getType() == Type.LIVE)
                            .filter(node -> !node.equals(currentNode.getNodeDefinition()))
                            .collect(Collectors.toList());
                    try {
                        send(chooseNode(nodes));
                    } catch (StatusRuntimeException e) {
                        log.error("Anti entropy request failed", e);
                    } catch (NoOtherNodesInClusterException e) {
                        log.info("No other nodes in cluster");
                    }
                },
                10,
                10,
                TimeUnit.SECONDS);
    }

    private void send(NodeDefinition node) {
        Integer port = Integer.valueOf(node.getAddress().split(":")[1]);
        // TODO: localhost
        log.info("Sending anti entropy request to localhost:{}", port);
        ClusterStateDiff diff = new InterNodeCommunicator("localhost", port).getClusterStateDiff(currentNode.getClusterState());
        currentNode.getClusterState().applyDiff(diff);
    }

    protected abstract NodeDefinition chooseNode(List<NodeDefinition> nodes) throws NoOtherNodesInClusterException;
}
