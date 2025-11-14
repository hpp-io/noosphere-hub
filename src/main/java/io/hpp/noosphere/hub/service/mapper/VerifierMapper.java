package io.hpp.noosphere.hub.service.mapper;

import io.hpp.noosphere.hub.domain.User;
import io.hpp.noosphere.hub.domain.Verifier;
import io.hpp.noosphere.hub.service.dto.UserDTO;
import io.hpp.noosphere.hub.service.dto.VerifierDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Verifier} and its DTO {@link VerifierDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface VerifierMapper extends EntityMapper<VerifierDTO, Verifier> {
  @Mapping(target = "createdByUser", source = "createdByUser", qualifiedByName = "userId")
  @Mapping(target = "updatedByUser", source = "updatedByUser", qualifiedByName = "userId")
  VerifierDTO toDto(Verifier s);

  @Named("userId")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  @Mapping(target = "name", source = "name") // Include other fields if needed
  UserDTO toDtoUserId(User user);
}
