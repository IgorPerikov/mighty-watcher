package com.github.igorperikov.heimdallr.domain;

import lombok.Getter;

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
        nodes.put(label, NodeDefinition.buildLiveDefinition(label, address));
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
