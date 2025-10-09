package io.hpp.noosphere.hub.service.dto;

import com.fasterxml.jackson.annotation.JsonView;
import io.hpp.noosphere.hub.domain.enumeration.StatusCode;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema
public class ContainerDTO implements Serializable {

    @JsonView(JsonViewType.Shallow.class)
    private UUID id;

    @JsonView(JsonViewType.Shallow.class)
    private String name;

    @NotNull
    @JsonView(JsonViewType.Update.class)
    private String walletAddress;

    @NotNull
    @JsonView(JsonViewType.Update.class)
    private BigDecimal price;

    @NotNull
    @JsonView(JsonViewType.Full.class)
    private StatusCode statusCode;

    @Lob
    @JsonView(JsonViewType.Update.class)
    private String description;

    @Lob
    @JsonView(JsonViewType.Update.class)
    private String parameters;

    @JsonView(JsonViewType.Full.class)
    private Instant createdAt;

    @JsonView(JsonViewType.Full.class)
    private Instant updatedAt;

    @JsonView(JsonViewType.Full.class)
    private UserDTO createdByUser;

    @JsonView(JsonViewType.Full.class)
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
