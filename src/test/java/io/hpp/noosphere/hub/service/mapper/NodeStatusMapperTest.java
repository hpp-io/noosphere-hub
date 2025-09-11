package io.hpp.noosphere.hub.service.mapper;

import static io.hpp.noosphere.hub.domain.NodeStatusAsserts.*;
import static io.hpp.noosphere.hub.domain.NodeStatusTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NodeStatusMapperTest {

    private NodeStatusMapper nodeStatusMapper;

    @BeforeEach
    void setUp() {
        nodeStatusMapper = new NodeStatusMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getNodeStatusSample1();
        var actual = nodeStatusMapper.toEntity(nodeStatusMapper.toDto(expected));
        assertNodeStatusAllPropertiesEquals(expected, actual);
    }
}
