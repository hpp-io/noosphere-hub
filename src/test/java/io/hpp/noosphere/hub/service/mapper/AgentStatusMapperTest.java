package io.hpp.noosphere.hub.service.mapper;

import static io.hpp.noosphere.hub.domain.AgentStatusAsserts.*;
import static io.hpp.noosphere.hub.domain.AgentStatusTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AgentStatusMapperTest {

    private AgentStatusMapper agentStatusMapper;

    @BeforeEach
    void setUp() {
        agentStatusMapper = new AgentStatusMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAgentStatusSample1();
        var actual = agentStatusMapper.toEntity(agentStatusMapper.toDto(expected));
        assertAgentStatusAllPropertiesEquals(expected, actual);
    }
}
