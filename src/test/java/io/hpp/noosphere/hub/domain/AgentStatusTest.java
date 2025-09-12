package io.hpp.noosphere.hub.domain;

import static io.hpp.noosphere.hub.domain.AgentStatusTestSamples.*;
import static io.hpp.noosphere.hub.domain.AgentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.hpp.noosphere.hub.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AgentStatusTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AgentStatus.class);
        AgentStatus agentStatus1 = getAgentStatusSample1();
        AgentStatus agentStatus2 = new AgentStatus();
        assertThat(agentStatus1).isNotEqualTo(agentStatus2);

        agentStatus2.setId(agentStatus1.getId());
        assertThat(agentStatus1).isEqualTo(agentStatus2);

        agentStatus2 = getAgentStatusSample2();
        assertThat(agentStatus1).isNotEqualTo(agentStatus2);
    }

    @Test
    void agentTest() {
        AgentStatus agentStatus = getAgentStatusRandomSampleGenerator();
        Agent agentBack = getAgentRandomSampleGenerator();

        agentStatus.setAgent(agentBack);
        assertThat(agentStatus.getAgent()).isEqualTo(agentBack);

        agentStatus.agent(null);
        assertThat(agentStatus.getAgent()).isNull();
    }
}
