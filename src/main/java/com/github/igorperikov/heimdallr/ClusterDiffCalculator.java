package com.github.igorperikov.heimdallr;

import com.github.igorperikov.heimdallr.domain.ClusterState;
import com.github.igorperikov.heimdallr.domain.ClusterStateDiff;
import com.github.igorperikov.heimdallr.domain.NodeDefinition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClusterDiffCalculator {
    public static ClusterStateDiff calculate(ClusterState first, ClusterState second) {
        Map<String, NodeDefinition> resultDiffMap = new HashMap<>();

        Map<String, NodeDefinition> firstNodesMap = first.getNodes();
        Map<String, NodeDefinition> secondNodesMap = second.getNodes();
        for (NodeDefinition firstNode : firstNodesMap.values()) {
            String label = firstNode.getLabel();
            if (secondNodesMap.containsKey(label)) {
                NodeDefinition secondNode = secondNodesMap.get(label);
                if (firstNode.equals(secondNode)) continue; // TODO: https://github.com/IgorPerikov/heimdallr/issues/12
                if (firstNode.getTimestamp().isAfter(secondNode.getTimestamp())) {
                    resultDiffMap.put(label, firstNode);
                } else {
                    resultDiffMap.put(label, secondNode);
                }
            } else {
                resultDiffMap.put(label, firstNode);
            }
        }
        Set<String> labelsInSecondState = new HashSet<>(secondNodesMap.keySet());
        labelsInSecondState.removeAll(firstNodesMap.keySet());
        labelsInSecondState.forEach(s -> resultDiffMap.put(s, secondNodesMap.get(s)));

        return new ClusterStateDiff(resultDiffMap);
    }
}
