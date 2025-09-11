package io.hpp.noosphere.hub.service.mapper;

import io.hpp.noosphere.hub.domain.Container;
import io.hpp.noosphere.hub.domain.Node;
import io.hpp.noosphere.hub.domain.NodeContainer;
import io.hpp.noosphere.hub.service.dto.ContainerDTO;
import io.hpp.noosphere.hub.service.dto.NodeContainerDTO;
import io.hpp.noosphere.hub.service.dto.NodeDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link NodeContainer} and its DTO {@link NodeContainerDTO}.
 */
@Mapper(componentModel = "spring")
public interface NodeContainerMapper extends EntityMapper<NodeContainerDTO, NodeContainer> {
    @Mapping(target = "node", source = "node", qualifiedByName = "nodeId")
    @Mapping(target = "container", source = "container", qualifiedByName = "containerId")
    NodeContainerDTO toDto(NodeContainer s);

    @Named("nodeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    NodeDTO toDtoNodeId(Node node);

    @Named("containerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ContainerDTO toDtoContainerId(Container container);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
