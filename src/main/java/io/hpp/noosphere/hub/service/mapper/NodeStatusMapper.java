package io.hpp.noosphere.hub.service.mapper;

import io.hpp.noosphere.hub.domain.Node;
import io.hpp.noosphere.hub.domain.NodeStatus;
import io.hpp.noosphere.hub.service.dto.NodeDTO;
import io.hpp.noosphere.hub.service.dto.NodeStatusDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link NodeStatus} and its DTO {@link NodeStatusDTO}.
 */
@Mapper(componentModel = "spring")
public interface NodeStatusMapper extends EntityMapper<NodeStatusDTO, NodeStatus> {
    @Mapping(target = "node", source = "node", qualifiedByName = "nodeId")
    NodeStatusDTO toDto(NodeStatus s);

    @Named("nodeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    NodeDTO toDtoNodeId(Node node);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
