package io.hpp.noosphere.hub.domain;

import static io.hpp.noosphere.hub.domain.AgentContainerTestSamples.*;
import static io.hpp.noosphere.hub.domain.ContainerTestSamples.*;
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
    void agentContainerTest() {
        Container container = getContainerRandomSampleGenerator();
        AgentContainer agentContainerBack = getAgentContainerRandomSampleGenerator();

        container.addAgentContainer(agentContainerBack);
        assertThat(container.getAgentContainers()).containsOnly(agentContainerBack);
        assertThat(agentContainerBack.getContainer()).isEqualTo(container);

        container.removeAgentContainer(agentContainerBack);
        assertThat(container.getAgentContainers()).doesNotContain(agentContainerBack);
        assertThat(agentContainerBack.getContainer()).isNull();

        container.agentContainers(new HashSet<>(Set.of(agentContainerBack)));
        assertThat(container.getAgentContainers()).containsOnly(agentContainerBack);
        assertThat(agentContainerBack.getContainer()).isEqualTo(container);

        container.setAgentContainers(new HashSet<>());
        assertThat(container.getAgentContainers()).doesNotContain(agentContainerBack);
        assertThat(agentContainerBack.getContainer()).isNull();
    }
}
