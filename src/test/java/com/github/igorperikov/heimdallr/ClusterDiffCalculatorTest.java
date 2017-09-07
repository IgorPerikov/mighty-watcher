package com.github.igorperikov.heimdallr;

import com.github.igorperikov.heimdallr.generated.ClusterStateDiffTO;
import com.github.igorperikov.heimdallr.generated.ClusterStateTO;
import com.github.igorperikov.heimdallr.generated.NodeDefinitionTO;
import com.github.igorperikov.heimdallr.generated.Type;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class ClusterDiffCalculatorTest {
    private final ClusterDiffCalculator diffCalculator = new ClusterDiffCalculator();

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
        String past = Instant.now().minus(1, ChronoUnit.DAYS).toString();
        String now = Instant.now().toString();

        NodeDefinitionTO oldDef = NodeDefinitionTO.newBuilder()
                .setLabel(label1)
                .setAddress(address1)
                .setTimestamp(past)
                .setType(Type.LIVE)
                .build();
        ClusterStateTO oldState = ClusterStateTO.newBuilder()
                .putNodes(oldDef.getLabel(), oldDef)
                .build();

        NodeDefinitionTO newDef = NodeDefinitionTO.newBuilder()
                .setLabel(label1)
                .setAddress(address1)
                .setTimestamp(now)
                .setType(Type.TOMBSTONE)
                .build();
        ClusterStateTO newState = ClusterStateTO.newBuilder()
                .putNodes(newDef.getLabel(), newDef)
                .build();

        ClusterStateDiffTO diff = diffCalculator.calculate(oldState, newState);
        assertEquals(now, diff.getNodesMap().get(label1).getTimestamp());
        assertEquals(Type.TOMBSTONE, diff.getNodesMap().get(label1).getType());

        ClusterStateDiffTO invertedParametersDiff = diffCalculator.calculate(newState, oldState);
        assertEquals(now, invertedParametersDiff.getNodesMap().get(label1).getTimestamp());
        assertEquals(Type.TOMBSTONE, invertedParametersDiff.getNodesMap().get(label1).getType());
    }

    @Test
    public void shouldAlwaysAddInfoAboutAbsentNodes() {
        NodeDefinitionTO firstDef = NodeDefinitionTO.newBuilder()
                .setLabel(label1)
                .setAddress(address1)
                .build();
        ClusterStateTO firstState = ClusterStateTO.newBuilder()
                .putNodes(firstDef.getLabel(), firstDef)
                .build();

        NodeDefinitionTO secondDef = NodeDefinitionTO.newBuilder()
                .setLabel(label2)
                .setAddress(address2)
                .build();
        ClusterStateTO secondState = ClusterStateTO.newBuilder()
                .putNodes(secondDef.getLabel(), secondDef)
                .build();

        ClusterStateDiffTO diff = diffCalculator.calculate(firstState, secondState);
        assertEquals(2, diff.getNodesMap().values().size());

        ClusterStateDiffTO invertedParametersDiff = diffCalculator.calculate(secondState, firstState);
        assertEquals(2, invertedParametersDiff.getNodesMap().values().size());
    }

    @Test
    public void sameNodeDefinitionShouldNotBeAddedToDiff() {
        NodeDefinitionTO nodeDef = NodeDefinitionTO.newBuilder()
                .setLabel(label1)
                .setAddress(address1)
                .build();

        ClusterStateTO firstState = ClusterStateTO.newBuilder()
                .putNodes(nodeDef.getLabel(), nodeDef)
                .build();

        ClusterStateTO secondState = ClusterStateTO.newBuilder()
                .putNodes(nodeDef.getLabel(), nodeDef)
                .build();

        ClusterStateDiffTO diff = diffCalculator.calculate(firstState, secondState);
        assertEquals(0, diff.getNodesMap().values().size());

        ClusterStateDiffTO invertedParametersDiff = diffCalculator.calculate(secondState, firstState);
        assertEquals(0, invertedParametersDiff.getNodesMap().values().size());
    }
}
