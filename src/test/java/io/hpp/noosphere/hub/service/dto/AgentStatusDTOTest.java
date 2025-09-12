package io.hpp.noosphere.hub.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.hpp.noosphere.hub.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AgentStatusDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AgentStatusDTO.class);
        AgentStatusDTO agentStatusDTO1 = new AgentStatusDTO();
        agentStatusDTO1.setId(UUID.randomUUID());
        AgentStatusDTO agentStatusDTO2 = new AgentStatusDTO();
        assertThat(agentStatusDTO1).isNotEqualTo(agentStatusDTO2);
        agentStatusDTO2.setId(agentStatusDTO1.getId());
        assertThat(agentStatusDTO1).isEqualTo(agentStatusDTO2);
        agentStatusDTO2.setId(UUID.randomUUID());
        assertThat(agentStatusDTO1).isNotEqualTo(agentStatusDTO2);
        agentStatusDTO1.setId(null);
        assertThat(agentStatusDTO1).isNotEqualTo(agentStatusDTO2);
    }
}
