package io.hpp.noosphere.hub.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link io.hpp.noosphere.hub.domain.Container} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ContainerDTO implements Serializable {

    @NotNull
    private UUID id;

    private String name;

    @NotNull
    private String walletAddress;

    @NotNull
    private BigDecimal price;

    @NotNull
    @Size(max = 20)
    private String statusCode;

    @Lob
    private String description;

    @Lob
    private String parameters;

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

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
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
        if (!(o instanceof ContainerDTO)) {
            return false;
        }

        ContainerDTO containerDTO = (ContainerDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, containerDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ContainerDTO{" +
            "id='" + getId() + "'" +
            ", name='" + getName() + "'" +
            ", walletAddress='" + getWalletAddress() + "'" +
            ", price=" + getPrice() +
            ", statusCode='" + getStatusCode() + "'" +
            ", description='" + getDescription() + "'" +
            ", parameters='" + getParameters() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", createdByUser=" + getCreatedByUser() +
            ", updatedByUser=" + getUpdatedByUser() +
            "}";
    }
}
