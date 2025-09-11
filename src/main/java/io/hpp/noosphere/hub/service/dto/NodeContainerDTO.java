package io.hpp.noosphere.hub.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link io.hpp.noosphere.hub.domain.NodeContainer} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NodeContainerDTO implements Serializable {

    @NotNull
    private UUID id;

    @NotNull
    @Size(max = 20)
    private String statusCode;

    @NotNull
    private Instant createdAt;

    private Instant updatedAt;

    private NodeDTO node;

    private ContainerDTO container;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public NodeDTO getNode() {
        return node;
    }

    public void setNode(NodeDTO node) {
        this.node = node;
    }

    public ContainerDTO getContainer() {
        return container;
    }

    public void setContainer(ContainerDTO container) {
        this.container = container;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NodeContainerDTO)) {
            return false;
        }

        NodeContainerDTO nodeContainerDTO = (NodeContainerDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, nodeContainerDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NodeContainerDTO{" +
            "id='" + getId() + "'" +
            ", statusCode='" + getStatusCode() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", node=" + getNode() +
            ", container=" + getContainer() +
            "}";
    }
}
