package com.github.igorperikov.heimdallr;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.junit.Assert.*;

// TODO: move to integration test
public class PeerNodeTest {
    @Test
    public void shouldCorrectlyRegisterNewNode() throws InterruptedException {
        HeimdallrNode mainNode = new HeimdallrNode(10000);
        HeimdallrNode secondNode = new HeimdallrNode(10001, "localhost", 10000);
        new Thread(mainNode::start).start();
        new Thread(secondNode::start).start();

        Thread.sleep(1000); // TODO: better way to wait?

//        assertEquals(mainNode.getClusterState().getNodesMap().values(), secondNode.getClusterState().getNodesMap().values());
        assertEquals(2, mainNode.getClusterState().getNodesMap().size());
        assertEquals(2, secondNode.getClusterState().getNodesMap().size());

//        assertThat(mainNode.getClusterState().getNodesMap().values(), CoreMatchers.hasItem(mainNode.getNodeDefinition()));
//        assertThat(mainNode.getClusterState().getNodesMap().values(), CoreMatchers.hasItem(secondNode.getNodeDefinition()));

//        assertThat(secondNode.getClusterState().getNodesMap().values(), CoreMatchers.hasItem(mainNode.getNodeDefinition()));
//        assertThat(secondNode.getClusterState().getNodesMap().values(), CoreMatchers.hasItem(mainNode.getNodeDefinition()));
    }
}
