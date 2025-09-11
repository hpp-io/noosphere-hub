package io.hpp.noosphere.hub.domain;

import static io.hpp.noosphere.hub.domain.NodeStatusTestSamples.*;
import static io.hpp.noosphere.hub.domain.NodeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.hpp.noosphere.hub.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NodeStatusTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(NodeStatus.class);
        NodeStatus nodeStatus1 = getNodeStatusSample1();
        NodeStatus nodeStatus2 = new NodeStatus();
        assertThat(nodeStatus1).isNotEqualTo(nodeStatus2);

        nodeStatus2.setId(nodeStatus1.getId());
        assertThat(nodeStatus1).isEqualTo(nodeStatus2);

        nodeStatus2 = getNodeStatusSample2();
        assertThat(nodeStatus1).isNotEqualTo(nodeStatus2);
    }

    @Test
    void nodeTest() {
        NodeStatus nodeStatus = getNodeStatusRandomSampleGenerator();
        Node nodeBack = getNodeRandomSampleGenerator();

        nodeStatus.setNode(nodeBack);
        assertThat(nodeStatus.getNode()).isEqualTo(nodeBack);

        nodeStatus.node(null);
        assertThat(nodeStatus.getNode()).isNull();
    }
}
