package io.hpp.noosphere.hub.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.hpp.noosphere.hub.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class NodeContainerDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(NodeContainerDTO.class);
        NodeContainerDTO nodeContainerDTO1 = new NodeContainerDTO();
        nodeContainerDTO1.setId(UUID.randomUUID());
        NodeContainerDTO nodeContainerDTO2 = new NodeContainerDTO();
        assertThat(nodeContainerDTO1).isNotEqualTo(nodeContainerDTO2);
        nodeContainerDTO2.setId(nodeContainerDTO1.getId());
        assertThat(nodeContainerDTO1).isEqualTo(nodeContainerDTO2);
        nodeContainerDTO2.setId(UUID.randomUUID());
        assertThat(nodeContainerDTO1).isNotEqualTo(nodeContainerDTO2);
        nodeContainerDTO1.setId(null);
        assertThat(nodeContainerDTO1).isNotEqualTo(nodeContainerDTO2);
    }
}
