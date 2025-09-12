package io.hpp.noosphere.hub.service.mapper;

import io.hpp.noosphere.hub.domain.Agent;
import io.hpp.noosphere.hub.domain.AgentContainer;
import io.hpp.noosphere.hub.domain.Container;
import io.hpp.noosphere.hub.service.dto.AgentContainerDTO;
import io.hpp.noosphere.hub.service.dto.AgentDTO;
import io.hpp.noosphere.hub.service.dto.ContainerDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AgentContainer} and its DTO {@link AgentContainerDTO}.
 */
@Mapper(componentModel = "spring")
public interface AgentContainerMapper extends EntityMapper<AgentContainerDTO, AgentContainer> {
    @Mapping(target = "node", source = "node", qualifiedByName = "agentId")
    @Mapping(target = "container", source = "container", qualifiedByName = "containerId")
    AgentContainerDTO toDto(AgentContainer s);

    @Named("agentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AgentDTO toDtoAgentId(Agent agent);

    @Named("containerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ContainerDTO toDtoContainerId(Container container);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
