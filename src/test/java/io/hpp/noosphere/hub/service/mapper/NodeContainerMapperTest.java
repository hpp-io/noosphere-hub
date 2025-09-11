package io.hpp.noosphere.hub.service.mapper;

import static io.hpp.noosphere.hub.domain.NodeContainerAsserts.*;
import static io.hpp.noosphere.hub.domain.NodeContainerTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NodeContainerMapperTest {

    private NodeContainerMapper nodeContainerMapper;

    @BeforeEach
    void setUp() {
        nodeContainerMapper = new NodeContainerMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getNodeContainerSample1();
        var actual = nodeContainerMapper.toEntity(nodeContainerMapper.toDto(expected));
        assertNodeContainerAllPropertiesEquals(expected, actual);
    }
}
