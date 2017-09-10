package com.github.igorperikov.heimdallr;

import com.github.igorperikov.heimdallr.domain.ClusterState;
import com.github.igorperikov.heimdallr.domain.ClusterStateDiff;
import com.github.igorperikov.heimdallr.domain.NodeDefinition;
import com.github.igorperikov.heimdallr.generated.Type;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class ClusterDiffCalculatorTest {
    private String label1;
    private String label2;
    private String address1;
    private String address2;

    @Before
    public void setup() {
        label1 = UUID.randomUUID().toString();
        label2 = UUID.randomUUID().toString();
        address1 = new InetSocketAddress("localhost", 1234).toString();
        address2 = new InetSocketAddress("localhost", 2345).toString();
    }

    @Test
    public void newEventsShouldOverrideOld() {
        Instant past = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant now = Instant.now();
        NodeDefinition nodeDef1 = new NodeDefinition(label1, address1, now, Type.LIVE);
        NodeDefinition nodeDef2 = new NodeDefinition(label1, address1, past, Type.LIVE);

        ClusterState firstState = new ClusterState(nodeDef1);
        ClusterState secondState = new ClusterState(nodeDef2);

        ClusterStateDiff diff = ClusterDiffCalculator.calculate(firstState, secondState);
        assertEquals(now, diff.getNodes().get(label1).getTimestamp());

        ClusterStateDiff invertedParametersDiff = ClusterDiffCalculator.calculate(secondState, firstState);
        assertEquals(now, invertedParametersDiff.getNodes().get(label1).getTimestamp());
    }

    @Test
    public void shouldAlwaysAddInfoAboutAbsentNodes() {
        NodeDefinition firstDef = NodeDefinition.buildLiveDefinition(label1, address1);
        ClusterState firstState = new ClusterState(firstDef);

        NodeDefinition secondDef = NodeDefinition.buildLiveDefinition(label2, address2);
        ClusterState secondState = new ClusterState(secondDef);

        ClusterStateDiff diff = ClusterDiffCalculator.calculate(firstState, secondState);
        assertEquals(2, diff.getNodes().values().size());

        ClusterStateDiff invertedParametersDiff = ClusterDiffCalculator.calculate(secondState, firstState);
        assertEquals(2, invertedParametersDiff.getNodes().values().size());
    }

    @Test
    public void sameNodeDefinitionShouldNotBeAddedToDiff() {
        NodeDefinition nodeDef = NodeDefinition.buildLiveDefinition(label1, address1);

        ClusterState firstState = new ClusterState(nodeDef);
        ClusterState secondState = new ClusterState(nodeDef);

        ClusterStateDiff diff = ClusterDiffCalculator.calculate(firstState, secondState);
        assertEquals(0, diff.getNodes().values().size());

        ClusterStateDiff invertedParametersDiff = ClusterDiffCalculator.calculate(secondState, firstState);
        assertEquals(0, invertedParametersDiff.getNodes().values().size());
    }
}
