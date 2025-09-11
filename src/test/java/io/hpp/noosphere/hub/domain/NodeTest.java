package io.hpp.noosphere.hub.domain;

import static io.hpp.noosphere.hub.domain.NodeContainerTestSamples.*;
import static io.hpp.noosphere.hub.domain.NodeStatusTestSamples.*;
import static io.hpp.noosphere.hub.domain.NodeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.hpp.noosphere.hub.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class NodeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Node.class);
        Node node1 = getNodeSample1();
        Node node2 = new Node();
        assertThat(node1).isNotEqualTo(node2);

        node2.setId(node1.getId());
        assertThat(node1).isEqualTo(node2);

        node2 = getNodeSample2();
        assertThat(node1).isNotEqualTo(node2);
    }

    @Test
    void nodeContainerTest() {
        Node node = getNodeRandomSampleGenerator();
        NodeContainer nodeContainerBack = getNodeContainerRandomSampleGenerator();

        node.addNodeContainer(nodeContainerBack);
        assertThat(node.getNodeContainers()).containsOnly(nodeContainerBack);
        assertThat(nodeContainerBack.getNode()).isEqualTo(node);

        node.removeNodeContainer(nodeContainerBack);
        assertThat(node.getNodeContainers()).doesNotContain(nodeContainerBack);
        assertThat(nodeContainerBack.getNode()).isNull();

        node.nodeContainers(new HashSet<>(Set.of(nodeContainerBack)));
        assertThat(node.getNodeContainers()).containsOnly(nodeContainerBack);
        assertThat(nodeContainerBack.getNode()).isEqualTo(node);

        node.setNodeContainers(new HashSet<>());
        assertThat(node.getNodeContainers()).doesNotContain(nodeContainerBack);
        assertThat(nodeContainerBack.getNode()).isNull();
    }

    @Test
    void nodeStatusTest() {
        Node node = getNodeRandomSampleGenerator();
        NodeStatus nodeStatusBack = getNodeStatusRandomSampleGenerator();

        node.setNodeStatus(nodeStatusBack);
        assertThat(node.getNodeStatus()).isEqualTo(nodeStatusBack);
        assertThat(nodeStatusBack.getNode()).isEqualTo(node);

        node.nodeStatus(null);
        assertThat(node.getNodeStatus()).isNull();
        assertThat(nodeStatusBack.getNode()).isNull();
    }
}
