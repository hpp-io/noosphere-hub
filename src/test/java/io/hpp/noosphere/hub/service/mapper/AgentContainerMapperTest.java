package io.hpp.noosphere.hub.service.mapper;

import static io.hpp.noosphere.hub.domain.AgentContainerAsserts.*;
import static io.hpp.noosphere.hub.domain.AgentContainerTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AgentContainerMapperTest {

    private AgentContainerMapper agentContainerMapper;

    @BeforeEach
    void setUp() {
        agentContainerMapper = new AgentContainerMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAgentContainerSample1();
        var actual = agentContainerMapper.toEntity(agentContainerMapper.toDto(expected));
        assertAgentContainerAllPropertiesEquals(expected, actual);
    }
}
