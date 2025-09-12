package io.hpp.noosphere.hub.domain;

import static io.hpp.noosphere.hub.domain.AgentContainerTestSamples.*;
import static io.hpp.noosphere.hub.domain.AgentStatusTestSamples.*;
import static io.hpp.noosphere.hub.domain.AgentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.hpp.noosphere.hub.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class AgentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Agent.class);
        Agent agent1 = getAgentSample1();
        Agent agent2 = new Agent();
        assertThat(agent1).isNotEqualTo(agent2);

        agent2.setId(agent1.getId());
        assertThat(agent1).isEqualTo(agent2);

        agent2 = getAgentSample2();
        assertThat(agent1).isNotEqualTo(agent2);
    }

    @Test
    void agentContainerTest() {
        Agent agent = getAgentRandomSampleGenerator();
        AgentContainer agentContainerBack = getAgentContainerRandomSampleGenerator();

        agent.addAgentContainer(agentContainerBack);
        assertThat(agent.getAgentContainers()).containsOnly(agentContainerBack);
        assertThat(agentContainerBack.getNode()).isEqualTo(agent);

        agent.removeAgentContainer(agentContainerBack);
        assertThat(agent.getAgentContainers()).doesNotContain(agentContainerBack);
        assertThat(agentContainerBack.getNode()).isNull();

        agent.agentContainers(new HashSet<>(Set.of(agentContainerBack)));
        assertThat(agent.getAgentContainers()).containsOnly(agentContainerBack);
        assertThat(agentContainerBack.getNode()).isEqualTo(agent);

        agent.setAgentContainers(new HashSet<>());
        assertThat(agent.getAgentContainers()).doesNotContain(agentContainerBack);
        assertThat(agentContainerBack.getNode()).isNull();
    }

    @Test
    void agentStatusTest() {
        Agent agent = getAgentRandomSampleGenerator();
        AgentStatus agentStatusBack = getAgentStatusRandomSampleGenerator();

        agent.setAgentStatus(agentStatusBack);
        assertThat(agent.getAgentStatus()).isEqualTo(agentStatusBack);
        assertThat(agentStatusBack.getAgent()).isEqualTo(agent);

        agent.agentStatus(null);
        assertThat(agent.getAgentStatus()).isNull();
        assertThat(agentStatusBack.getAgent()).isNull();
    }
}
