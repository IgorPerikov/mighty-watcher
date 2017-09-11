package com.github.igorperikov.heimdallr.epidemics;

import com.github.igorperikov.heimdallr.HeimdallrNode;
import com.github.igorperikov.heimdallr.domain.NodeDefinition;

import java.util.List;
import java.util.Random;

public class RandomNodeAntiEntropyMechanism extends AntiEntropyMechanism {
    private static final Random RANDOM = new Random();

    public RandomNodeAntiEntropyMechanism(HeimdallrNode mainNode) {
        super(mainNode);
    }

    @Override
    protected NodeDefinition chooseNode(List<NodeDefinition> nodes) {
        return nodes.get(RANDOM.nextInt(nodes.size()));
    }
}
