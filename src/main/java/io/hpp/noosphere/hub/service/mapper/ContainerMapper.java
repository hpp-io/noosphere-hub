package io.hpp.noosphere.hub.service.mapper;

import io.hpp.noosphere.hub.domain.Container;
import io.hpp.noosphere.hub.domain.User;
import io.hpp.noosphere.hub.service.dto.ContainerDTO;
import io.hpp.noosphere.hub.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Container} and its DTO {@link ContainerDTO}.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ContainerMapper extends EntityMapper<ContainerDTO, Container> {
    @Mapping(target = "createdByUser", source = "createdByUser", qualifiedByName = "userId")
    @Mapping(target = "updatedByUser", source = "updatedByUser", qualifiedByName = "userId")
    ContainerDTO toDto(Container s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name") // Include other fields if needed
    @Mapping(target = "authorities", ignore = true) //
    UserDTO toDtoUserId(User user);
}
