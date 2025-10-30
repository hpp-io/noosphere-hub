package io.hpp.noosphere.hub.service.mapper;

import io.hpp.noosphere.hub.domain.Validator;
import io.hpp.noosphere.hub.domain.User;
import io.hpp.noosphere.hub.service.dto.ValidatorDTO;
import io.hpp.noosphere.hub.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Validator} and its DTO {@link ValidatorDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface ValidatorMapper extends EntityMapper<ValidatorDTO, Validator> {
    @Mapping(target = "createdByUser", source = "createdByUser", qualifiedByName = "userId")
    @Mapping(target = "updatedByUser", source = "updatedByUser", qualifiedByName = "userId")
    ValidatorDTO toDto(Validator s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name") // Include other fields if needed
    UserDTO toDtoUserId(User user);
}
