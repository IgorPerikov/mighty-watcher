package com.github.igorperikov.heimdallr.domain;

import com.github.igorperikov.heimdallr.generated.Type;
import lombok.Getter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ClusterState {
    @Getter
    private final Map<String, NodeDefinition> nodes = new HashMap<>();

    public ClusterState(Map<String, NodeDefinition> nodes) {
        this.nodes.putAll(nodes);
    }

    public ClusterState(NodeDefinition nodeDefinition) {
        nodes.put(nodeDefinition.getLabel(), nodeDefinition);
    }

    public ClusterState(String label, String address) {
        NodeDefinition nodeDefinition = new NodeDefinition(label, address, Instant.now(), Type.LIVE);
        nodes.put(label, nodeDefinition);
    }

    /**
     * applies diff without any checks
     */
    public void applyDiff(ClusterStateDiff diff) {
        nodes.putAll(diff.getNodes());
    }

    @Override
    public String toString() {
        return nodes.values().stream()
                .map(NodeDefinition::toString)
                .collect(Collectors.joining("\r\n"));
    }
}
