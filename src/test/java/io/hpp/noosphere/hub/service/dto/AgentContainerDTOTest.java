package io.hpp.noosphere.hub.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.hpp.noosphere.hub.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AgentContainerDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AgentContainerDTO.class);
        AgentContainerDTO agentContainerDTO1 = new AgentContainerDTO();
        agentContainerDTO1.setId(UUID.randomUUID());
        AgentContainerDTO agentContainerDTO2 = new AgentContainerDTO();
        assertThat(agentContainerDTO1).isNotEqualTo(agentContainerDTO2);
        agentContainerDTO2.setId(agentContainerDTO1.getId());
        assertThat(agentContainerDTO1).isEqualTo(agentContainerDTO2);
        agentContainerDTO2.setId(UUID.randomUUID());
        assertThat(agentContainerDTO1).isNotEqualTo(agentContainerDTO2);
        agentContainerDTO1.setId(null);
        assertThat(agentContainerDTO1).isNotEqualTo(agentContainerDTO2);
    }
}
