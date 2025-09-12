package io.hpp.noosphere.hub.service.mapper;

import io.hpp.noosphere.hub.domain.Agent;
import io.hpp.noosphere.hub.domain.User;
import io.hpp.noosphere.hub.service.dto.AgentDTO;
import io.hpp.noosphere.hub.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Agent} and its DTO {@link AgentDTO}.
 */
@Mapper(componentModel = "spring")
public interface AgentMapper extends EntityMapper<AgentDTO, Agent> {
    @Mapping(target = "createdByUser", source = "createdByUser", qualifiedByName = "userId")
    @Mapping(target = "updatedByUser", source = "updatedByUser", qualifiedByName = "userId")
    AgentDTO toDto(Agent s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
