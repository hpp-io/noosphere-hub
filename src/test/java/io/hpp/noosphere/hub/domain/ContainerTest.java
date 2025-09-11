package io.hpp.noosphere.hub.domain;

import static io.hpp.noosphere.hub.domain.ContainerTestSamples.*;
import static io.hpp.noosphere.hub.domain.NodeContainerTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.hpp.noosphere.hub.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ContainerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Container.class);
        Container container1 = getContainerSample1();
        Container container2 = new Container();
        assertThat(container1).isNotEqualTo(container2);

        container2.setId(container1.getId());
        assertThat(container1).isEqualTo(container2);

        container2 = getContainerSample2();
        assertThat(container1).isNotEqualTo(container2);
    }

    @Test
    void nodeContainerTest() {
        Container container = getContainerRandomSampleGenerator();
        NodeContainer nodeContainerBack = getNodeContainerRandomSampleGenerator();

        container.addNodeContainer(nodeContainerBack);
        assertThat(container.getNodeContainers()).containsOnly(nodeContainerBack);
        assertThat(nodeContainerBack.getContainer()).isEqualTo(container);

        container.removeNodeContainer(nodeContainerBack);
        assertThat(container.getNodeContainers()).doesNotContain(nodeContainerBack);
        assertThat(nodeContainerBack.getContainer()).isNull();

        container.nodeContainers(new HashSet<>(Set.of(nodeContainerBack)));
        assertThat(container.getNodeContainers()).containsOnly(nodeContainerBack);
        assertThat(nodeContainerBack.getContainer()).isEqualTo(container);

        container.setNodeContainers(new HashSet<>());
        assertThat(container.getNodeContainers()).doesNotContain(nodeContainerBack);
        assertThat(nodeContainerBack.getContainer()).isNull();
    }
}
