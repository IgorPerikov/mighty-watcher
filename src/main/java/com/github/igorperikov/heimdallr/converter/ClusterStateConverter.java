package com.github.igorperikov.heimdallr.converter;

import com.github.igorperikov.heimdallr.domain.ClusterState;
import com.github.igorperikov.heimdallr.domain.NodeDefinition;
import com.github.igorperikov.heimdallr.generated.ClusterStateTO;
import com.github.igorperikov.heimdallr.generated.NodeDefinitionTO;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ClusterStateConverter {
    public static ClusterStateTO convertDomain(ClusterState domain) {
        Map<String, NodeDefinitionTO> transferObjectNodes = domain.getNodes().values().stream()
                .map(node -> {
                    return NodeDefinitionTO.newBuilder()
                            .setLabel(node.getLabel())
                            .setAddress(node.getAddress())
                            .setTimestamp(node.getTimestamp().toString())
                            .setType(node.getType())
                            .build();
                }).collect(Collectors.toMap(NodeDefinitionTO::getLabel, Function.identity()));
        return ClusterStateTO.newBuilder().putAllNodes(transferObjectNodes).build();
    }

    public static ClusterState convertToDomain(ClusterStateTO clusterStateTO) {
        Map<String, NodeDefinition> resultMap = clusterStateTO.getNodesMap().values().stream()
                .map(NodeDefinitionConverter::convertToDomain)
                .collect(Collectors.toMap(NodeDefinition::getLabel, Function.identity()));
        return new ClusterState(resultMap);
    }
}
