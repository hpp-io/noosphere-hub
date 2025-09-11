package io.hpp.noosphere.hub.service.mapper;

import static io.hpp.noosphere.hub.domain.ContainerAsserts.*;
import static io.hpp.noosphere.hub.domain.ContainerTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContainerMapperTest {

    private ContainerMapper containerMapper;

    @BeforeEach
    void setUp() {
        containerMapper = new ContainerMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getContainerSample1();
        var actual = containerMapper.toEntity(containerMapper.toDto(expected));
        assertContainerAllPropertiesEquals(expected, actual);
    }
}
