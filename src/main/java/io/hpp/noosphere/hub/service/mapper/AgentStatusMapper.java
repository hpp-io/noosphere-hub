package io.hpp.noosphere.hub.service.mapper;

import io.hpp.noosphere.hub.domain.Agent;
import io.hpp.noosphere.hub.domain.AgentStatus;
import io.hpp.noosphere.hub.service.dto.AgentDTO;
import io.hpp.noosphere.hub.service.dto.AgentStatusDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AgentStatus} and its DTO {@link AgentStatusDTO}.
 */
@Mapper(componentModel = "spring")
public interface AgentStatusMapper extends EntityMapper<AgentStatusDTO, AgentStatus> {
    @Mapping(target = "agent", source = "agent", qualifiedByName = "agentId")
    AgentStatusDTO toDto(AgentStatus s);

    @Named("agentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AgentDTO toDtoAgentId(Agent agent);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
