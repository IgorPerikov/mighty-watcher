package com.github.igorperikov.heimdallr;

import com.github.igorperikov.heimdallr.generated.ClusterStateDiffTO;
import com.github.igorperikov.heimdallr.generated.ClusterStateTO;
import com.github.igorperikov.heimdallr.generated.NodeDefinitionTO;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClusterDiffCalculator {
    public ClusterStateDiffTO calculate(ClusterStateTO first, ClusterStateTO second) {
        Map<String, NodeDefinitionTO> resultDiffMap = new HashMap<>();

        Map<String, NodeDefinitionTO> firstNodesMap = first.getNodesMap();
        Map<String, NodeDefinitionTO> secondNodesMap = second.getNodesMap();
        for (NodeDefinitionTO firstNode : firstNodesMap.values()) {
            String label = firstNode.getLabel();
            if (secondNodesMap.containsKey(label)) {
                NodeDefinitionTO secondNode = secondNodesMap.get(label);
                if (isSame(firstNode, secondNode)) continue;

                Instant firstDefinitionInstant = Instant.parse(firstNode.getTimestamp());
                Instant secondDefinitionInstant = Instant.parse(secondNode.getTimestamp());
                if (firstDefinitionInstant.isAfter(secondDefinitionInstant)) {
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

        return ClusterStateDiffTO.newBuilder().putAllNodes(resultDiffMap).build();
    }

    private boolean isSame(NodeDefinitionTO first, NodeDefinitionTO second) {
        return first.getLabel().equals(second.getLabel()) &&
                first.getType().getNumber() == second.getType().getNumber();
    }
}
