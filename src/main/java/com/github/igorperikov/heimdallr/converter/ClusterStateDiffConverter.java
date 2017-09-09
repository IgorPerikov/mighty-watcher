package com.github.igorperikov.heimdallr.converter;

import com.github.igorperikov.heimdallr.domain.ClusterStateDiff;
import com.github.igorperikov.heimdallr.domain.NodeDefinition;
import com.github.igorperikov.heimdallr.generated.ClusterStateDiffTO;
import com.github.igorperikov.heimdallr.generated.NodeDefinitionTO;

import java.util.Map;
import java.util.stream.Collectors;

public class ClusterStateDiffConverter {
    public static ClusterStateDiff convertToDomain(ClusterStateDiffTO diffTO) {
        Map<String, NodeDefinition> domainNodesMap = diffTO.getNodesMap().values()
                .stream()
                .collect(Collectors.toMap(NodeDefinitionTO::getLabel, NodeDefinitionConverter::convertToDomain));
        return new ClusterStateDiff(domainNodesMap);
    }

    public static ClusterStateDiffTO convertDomain(ClusterStateDiff diff) {
        Map<String, NodeDefinitionTO> toNodesMap = diff.getNodes().values().stream()
                .collect(Collectors.toMap(NodeDefinition::getLabel, NodeDefinitionConverter::convertDomain));
        return ClusterStateDiffTO.newBuilder()
                .putAllNodes(toNodesMap)
                .build();
    }
}
