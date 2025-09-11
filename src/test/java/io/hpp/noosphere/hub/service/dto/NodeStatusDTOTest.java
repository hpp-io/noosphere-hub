package io.hpp.noosphere.hub.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.hpp.noosphere.hub.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class NodeStatusDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(NodeStatusDTO.class);
        NodeStatusDTO nodeStatusDTO1 = new NodeStatusDTO();
        nodeStatusDTO1.setId(UUID.randomUUID());
        NodeStatusDTO nodeStatusDTO2 = new NodeStatusDTO();
        assertThat(nodeStatusDTO1).isNotEqualTo(nodeStatusDTO2);
        nodeStatusDTO2.setId(nodeStatusDTO1.getId());
        assertThat(nodeStatusDTO1).isEqualTo(nodeStatusDTO2);
        nodeStatusDTO2.setId(UUID.randomUUID());
        assertThat(nodeStatusDTO1).isNotEqualTo(nodeStatusDTO2);
        nodeStatusDTO1.setId(null);
        assertThat(nodeStatusDTO1).isNotEqualTo(nodeStatusDTO2);
    }
}
