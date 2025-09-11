package io.hpp.noosphere.hub.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link io.hpp.noosphere.hub.domain.Node} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NodeDTO implements Serializable {

    @NotNull
    private UUID id;

    private String name;

    @NotNull
    @Size(max = 1024)
    private String apiUrl;

    @NotNull
    private String apiKey;

    @NotNull
    @Size(max = 20)
    private String statusCode;

    @Lob
    private String description;

    @NotNull
    private Instant createdAt;

    private Instant updatedAt;

    private UserDTO createdByUser;

    private UserDTO updatedByUser;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public UserDTO getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(UserDTO createdByUser) {
        this.createdByUser = createdByUser;
    }

    public UserDTO getUpdatedByUser() {
        return updatedByUser;
    }

    public void setUpdatedByUser(UserDTO updatedByUser) {
        this.updatedByUser = updatedByUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NodeDTO)) {
            return false;
        }

        NodeDTO nodeDTO = (NodeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, nodeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NodeDTO{" +
            "id='" + getId() + "'" +
            ", name='" + getName() + "'" +
            ", apiUrl='" + getApiUrl() + "'" +
            ", apiKey='" + getApiKey() + "'" +
            ", statusCode='" + getStatusCode() + "'" +
            ", description='" + getDescription() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", createdByUser=" + getCreatedByUser() +
            ", updatedByUser=" + getUpdatedByUser() +
            "}";
    }
}
