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

        assertEquals(mainNode.getClusterNodes(), secondNode.getClusterNodes());
        assertEquals(2, mainNode.getClusterNodes().size());
        assertEquals(2, secondNode.getClusterNodes().size());

        assertThat(mainNode.getClusterNodes(), CoreMatchers.hasItem(mainNode.getNodeDefinition()));
        assertThat(mainNode.getClusterNodes(), CoreMatchers.hasItem(secondNode.getNodeDefinition()));

        assertThat(secondNode.getClusterNodes(), CoreMatchers.hasItem(mainNode.getNodeDefinition()));
        assertThat(secondNode.getClusterNodes(), CoreMatchers.hasItem(mainNode.getNodeDefinition()));
    }
}
