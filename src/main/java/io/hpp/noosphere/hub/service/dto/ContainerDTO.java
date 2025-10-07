package io.hpp.noosphere.hub.service.dto;

import io.hpp.noosphere.hub.domain.enumeration.StatusCode;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A DTO for the {@link io.hpp.noosphere.hub.domain.Container} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContainerDTO implements Serializable {

    @NotNull
    private UUID id;

    private String name;

    @NotNull
    private String walletAddress;

    @NotNull
    private BigDecimal price;

    @NotNull
    private StatusCode statusCode;

    @Lob
    private String description;

    @Lob
    private String parameters;

    @NotNull
    private Instant createdAt;

    private Instant updatedAt;

    private UserDTO createdByUser;

    private UserDTO updatedByUser;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ContainerDTO that)) {
            return false;
        }

      return new EqualsBuilder().append(id, that.id).append(name, that.name).append(walletAddress, that.walletAddress)
          .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(name).append(walletAddress)
          .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
          .append("id", id)
          .append("name", name)
          .append("walletAddress", walletAddress)
          .append("price", price)
          .append("statusCode", statusCode)
          .append("description", description)
          .append("parameters", parameters)
          .append("createdAt", createdAt)
          .append("updatedAt", updatedAt)
          .append("createdByUser", createdByUser !=null ? createdByUser.getId() : null)
          .append("updatedByUser", updatedByUser !=null ? updatedByUser.getId() : null)
          .toString();
    }
}
