package io.hpp.noosphere.hub.service.mapper;

import io.hpp.noosphere.hub.domain.Node;
import io.hpp.noosphere.hub.domain.User;
import io.hpp.noosphere.hub.service.dto.NodeDTO;
import io.hpp.noosphere.hub.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Node} and its DTO {@link NodeDTO}.
 */
@Mapper(componentModel = "spring")
public interface NodeMapper extends EntityMapper<NodeDTO, Node> {
    @Mapping(target = "createdByUser", source = "createdByUser", qualifiedByName = "userId")
    @Mapping(target = "updatedByUser", source = "updatedByUser", qualifiedByName = "userId")
    NodeDTO toDto(Node s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
