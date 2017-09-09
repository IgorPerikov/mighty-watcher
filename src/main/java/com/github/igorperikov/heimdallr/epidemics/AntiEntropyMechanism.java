package com.github.igorperikov.heimdallr.epidemics;

import com.github.igorperikov.heimdallr.ClusterStateMerger;
import com.github.igorperikov.heimdallr.HeimdallrNode;
import com.github.igorperikov.heimdallr.InterNodeCommunicator;
import com.github.igorperikov.heimdallr.generated.ClusterStateDiffTO;
import com.github.igorperikov.heimdallr.generated.ClusterStateTO;
import com.github.igorperikov.heimdallr.generated.NodeDefinitionTO;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@AllArgsConstructor
public abstract class AntiEntropyMechanism {
    private final HeimdallrNode currentNode;

    public ScheduledFuture launch() {
        return Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
                    ClusterStateTO clusterState = currentNode.getClusterState();
                    List<NodeDefinitionTO> nodes = clusterState.getNodesMap().entrySet().stream()
                            .filter(entry -> !entry.getKey().equals(currentNode.getLabel().toString()))
                            .map(Map.Entry::getValue)
                            .collect(Collectors.toList());
                    send(chooseNode(nodes));
                },
                10,
                10,
                TimeUnit.SECONDS);
    }

    private void send(NodeDefinitionTO node) {
        Integer port = Integer.valueOf(node.getAddress().split(":")[1]);
        // TODO: localhost
        ClusterStateDiffTO diff = new InterNodeCommunicator()
                .getClusterStateDiff(currentNode, "localhost", port);
        currentNode.setClusterState(new ClusterStateMerger().merge(currentNode.getClusterState(), diff));
    }

    protected abstract NodeDefinitionTO chooseNode(List<NodeDefinitionTO> nodes);
}
