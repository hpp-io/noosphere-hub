package io.hpp.noosphere.hub.service.mapper;

import io.hpp.noosphere.hub.domain.Container;
import io.hpp.noosphere.hub.domain.User;
import io.hpp.noosphere.hub.domain.UserSubscription;
import io.hpp.noosphere.hub.service.dto.ContainerDTO;
import io.hpp.noosphere.hub.service.dto.UserDTO;
import io.hpp.noosphere.hub.service.dto.UserSubscriptionDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link UserSubscription} and its DTO {@link UserSubscriptionDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class, ContainerMapper.class })
public interface UserSubscriptionMapper extends EntityMapper<UserSubscriptionDTO, UserSubscription> {
    @Mapping(target = "owner", source = "owner", qualifiedByName = "ownerId")
    @Mapping(target = "container", source = "container", qualifiedByName = "containerId")
    UserSubscriptionDTO toDto(UserSubscription s);

    @Named("ownerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name") // Include other fields if needed
    UserDTO toDtoUserId(User user);

    @Named("containerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ContainerDTO toDtoContainerId(Container container);
}
