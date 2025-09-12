package io.hpp.noosphere.hub.domain;

import static io.hpp.noosphere.hub.domain.AgentContainerTestSamples.*;
import static io.hpp.noosphere.hub.domain.AgentTestSamples.*;
import static io.hpp.noosphere.hub.domain.ContainerTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.hpp.noosphere.hub.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AgentContainerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AgentContainer.class);
        AgentContainer agentContainer1 = getAgentContainerSample1();
        AgentContainer agentContainer2 = new AgentContainer();
        assertThat(agentContainer1).isNotEqualTo(agentContainer2);

        agentContainer2.setId(agentContainer1.getId());
        assertThat(agentContainer1).isEqualTo(agentContainer2);

        agentContainer2 = getAgentContainerSample2();
        assertThat(agentContainer1).isNotEqualTo(agentContainer2);
    }

    @Test
    void nodeTest() {
        AgentContainer agentContainer = getAgentContainerRandomSampleGenerator();
        Agent agentBack = getAgentRandomSampleGenerator();

        agentContainer.setNode(agentBack);
        assertThat(agentContainer.getNode()).isEqualTo(agentBack);

        agentContainer.node(null);
        assertThat(agentContainer.getNode()).isNull();
    }

    @Test
    void containerTest() {
        AgentContainer agentContainer = getAgentContainerRandomSampleGenerator();
        Container containerBack = getContainerRandomSampleGenerator();

        agentContainer.setContainer(containerBack);
        assertThat(agentContainer.getContainer()).isEqualTo(containerBack);

        agentContainer.container(null);
        assertThat(agentContainer.getContainer()).isNull();
    }
}
