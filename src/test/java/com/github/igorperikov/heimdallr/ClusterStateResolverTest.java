package com.github.igorperikov.heimdallr;

import com.github.igorperikov.heimdallr.generated.ClusterStateTO;
import com.github.igorperikov.heimdallr.generated.NodeDefinitionTO;
import com.github.igorperikov.heimdallr.generated.Type;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.Assert.*;

public class ClusterStateResolverTest {
    private final ClusterStateResolver resolver = new ClusterStateResolver();

    @Test
    public void newEventsShouldOverrideOld() {
        String label = UUID.randomUUID().toString();
        String past = Instant.now().minus(1, ChronoUnit.DAYS).toString();
        String now = Instant.now().toString();
        String address = new InetSocketAddress("localhost", 1234).toString();

        NodeDefinitionTO oldDef = NodeDefinitionTO.newBuilder()
                .setLabel(label)
                .setAddress(address)
                .setTimestamp(past)
                .setType(Type.LIVE)
                .build();
        ClusterStateTO oldState = ClusterStateTO.newBuilder()
                .putNodes(oldDef.getLabel(), oldDef)
                .build();

        NodeDefinitionTO newDef = NodeDefinitionTO.newBuilder()
                .setLabel(label)
                .setAddress(address)
                .setTimestamp(now)
                .setType(Type.TOMBSTONE)
                .build();
        ClusterStateTO newState = ClusterStateTO.newBuilder()
                .putNodes(newDef.getLabel(), newDef)
                .build();

        ClusterStateTO resolvedState = resolver.resolve(oldState, newState);

        assertEquals(now, resolvedState.getNodesMap().get(label).getTimestamp());
        assertEquals(Type.TOMBSTONE, resolvedState.getNodesMap().get(label).getType());
    }

    @Test
    public void shouldAlwaysAddInfoAboutAbsentNodes() {
        String label1 = UUID.randomUUID().toString();
        String label2 = UUID.randomUUID().toString();
        String past = Instant.now().minus(1, ChronoUnit.DAYS).toString();
        String now = Instant.now().toString();
        String address1 = new InetSocketAddress("localhost", 1234).toString();
        String address2 = new InetSocketAddress("localhost", 2345).toString();

        NodeDefinitionTO firstDef = NodeDefinitionTO.newBuilder()
                .setLabel(label1)
                .setAddress(address1)
                .setTimestamp(past)
                .setType(Type.TOMBSTONE)
                .build();
        ClusterStateTO firstState = ClusterStateTO.newBuilder()
                .putNodes(firstDef.getLabel(), firstDef)
                .build();

        NodeDefinitionTO secondDef = NodeDefinitionTO.newBuilder()
                .setLabel(label2)
                .setAddress(address2)
                .setTimestamp(now)
                .setType(Type.LIVE)
                .build();
        ClusterStateTO secondState = ClusterStateTO.newBuilder()
                .putNodes(secondDef.getLabel(), secondDef)
                .build();

        ClusterStateTO resolved = resolver.resolve(firstState, secondState);
        assertEquals(2, resolved.getNodesMap().values().size());
    }
}
