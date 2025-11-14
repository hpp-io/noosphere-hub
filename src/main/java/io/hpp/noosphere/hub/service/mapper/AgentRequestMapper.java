package io.hpp.noosphere.hub.service.mapper;

import io.hpp.noosphere.hub.domain.Agent;
import io.hpp.noosphere.hub.domain.AgentRequest;
import io.hpp.noosphere.hub.domain.Container;
import io.hpp.noosphere.hub.domain.UserSubscription;
import io.hpp.noosphere.hub.service.dto.AgentDTO;
import io.hpp.noosphere.hub.service.dto.AgentRequestDTO;
import io.hpp.noosphere.hub.service.dto.ContainerDTO;
import io.hpp.noosphere.hub.service.dto.UserSubscriptionDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = { AgentMapper.class, ContainerMapper.class, UserSubscriptionMapper.class })
public interface AgentRequestMapper extends EntityMapper<AgentRequestDTO, AgentRequest> {
  @Mapping(target = "agent", source = "agent", qualifiedByName = "agentId")
  @Mapping(target = "container", source = "container", qualifiedByName = "containerId")
  @Mapping(target = "userSubscription", source = "userSubscription", qualifiedByName = "userSubscriptionId")
  AgentRequestDTO toDto(AgentRequest s);

  @Named("agentId")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  AgentDTO toDtoAgentId(Agent agent);

  @Named("userSubscriptionId")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  UserSubscriptionDTO toDtoUserSubscriptionId(UserSubscription userSubscription);

  default String map(UUID value) {
    return Objects.toString(value, null);
  }
}
