package io.hpp.noosphere.hub.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link io.hpp.noosphere.hub.domain.NodeStatus} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NodeStatusDTO implements Serializable {

    @NotNull
    private UUID id;

    @NotNull
    private Instant createdAt;

    private Instant lastKeepAliveAt;

    private NodeDTO node;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getLastKeepAliveAt() {
        return lastKeepAliveAt;
    }

    public void setLastKeepAliveAt(Instant lastKeepAliveAt) {
        this.lastKeepAliveAt = lastKeepAliveAt;
    }

    public NodeDTO getNode() {
        return node;
    }

    public void setNode(NodeDTO node) {
        this.node = node;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NodeStatusDTO)) {
            return false;
        }

        NodeStatusDTO nodeStatusDTO = (NodeStatusDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, nodeStatusDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NodeStatusDTO{" +
            "id='" + getId() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", lastKeepAliveAt='" + getLastKeepAliveAt() + "'" +
            ", node=" + getNode() +
            "}";
    }
}
