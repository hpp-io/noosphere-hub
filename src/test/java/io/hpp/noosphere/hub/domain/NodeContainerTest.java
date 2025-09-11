package io.hpp.noosphere.hub.domain;

import static io.hpp.noosphere.hub.domain.ContainerTestSamples.*;
import static io.hpp.noosphere.hub.domain.NodeContainerTestSamples.*;
import static io.hpp.noosphere.hub.domain.NodeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.hpp.noosphere.hub.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NodeContainerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(NodeContainer.class);
        NodeContainer nodeContainer1 = getNodeContainerSample1();
        NodeContainer nodeContainer2 = new NodeContainer();
        assertThat(nodeContainer1).isNotEqualTo(nodeContainer2);

        nodeContainer2.setId(nodeContainer1.getId());
        assertThat(nodeContainer1).isEqualTo(nodeContainer2);

        nodeContainer2 = getNodeContainerSample2();
        assertThat(nodeContainer1).isNotEqualTo(nodeContainer2);
    }

    @Test
    void nodeTest() {
        NodeContainer nodeContainer = getNodeContainerRandomSampleGenerator();
        Node nodeBack = getNodeRandomSampleGenerator();

        nodeContainer.setNode(nodeBack);
        assertThat(nodeContainer.getNode()).isEqualTo(nodeBack);

        nodeContainer.node(null);
        assertThat(nodeContainer.getNode()).isNull();
    }

    @Test
    void containerTest() {
        NodeContainer nodeContainer = getNodeContainerRandomSampleGenerator();
        Container containerBack = getContainerRandomSampleGenerator();

        nodeContainer.setContainer(containerBack);
        assertThat(nodeContainer.getContainer()).isEqualTo(containerBack);

        nodeContainer.container(null);
        assertThat(nodeContainer.getContainer()).isNull();
    }
}
