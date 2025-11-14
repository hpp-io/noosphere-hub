package io.hpp.noosphere.hub.service.dto;

import com.fasterxml.jackson.annotation.JsonView;
import io.hpp.noosphere.hub.domain.Verifier;
import io.hpp.noosphere.hub.domain.enumeration.StatusCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A DTO for the {@link Verifier} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema
public class VerifierDTO implements Serializable {

  @JsonView(JsonViewType.Shallow.class)
  private UUID id;

  @NotNull
  @JsonView(JsonViewType.Shallow.class)
  private String name;

  @NotNull
  @JsonView(JsonViewType.Update.class)
  private String walletAddress;

  @NotNull
  @JsonView(JsonViewType.Update.class)
  private String verifierAddress;

  @NotNull
  @JsonView(JsonViewType.Update.class)
  private String imageName;

  @NotNull
  @JsonView(JsonViewType.Update.class)
  private Integer port;

  @JsonView(JsonViewType.Update.class)
  private String command;

  @Lob
  @JsonView(JsonViewType.Update.class)
  private String environmentVariables;

  @Lob
  @JsonView(JsonViewType.Update.class)
  private String volumes;

  @Lob
  @JsonView(JsonViewType.Update.class)
  private String payments;

  @NotNull
  @JsonView(JsonViewType.Full.class)
  private StatusCode statusCode;

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

    if (!(o instanceof VerifierDTO that)) {
      return false;
    }

    return new EqualsBuilder()
      .append(id, that.id)
      .append(name, that.name)
      .append(verifierAddress, that.verifierAddress)
      .append(imageName, that.imageName)
      .append(port, that.port)
      .append(statusCode, that.statusCode)
      .append(createdAt, that.createdAt)
      .append(updatedAt, that.updatedAt)
      .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
      .append(id)
      .append(name)
      .append(verifierAddress)
      .append(imageName)
      .append(port)
      .append(statusCode)
      .append(createdAt)
      .append(updatedAt)
      .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
      .append("id", id)
      .append("name", name)
      .append("walletAddress", walletAddress)
      .append("verifierAddress", verifierAddress)
      .append("imageName", imageName)
      .append("port", port)
      .append("command", command)
      .append("environmentVariables", environmentVariables)
      .append("volumes", volumes)
      .append("payments", payments)
      .append("statusCode", statusCode)
      .append("createdAt", createdAt)
      .append("updatedAt", updatedAt)
      .append("createdByUser", createdByUser != null ? createdByUser.getId() : null)
      .append("updatedByUser", updatedByUser != null ? updatedByUser.getId() : null)
      .toString();
  }
}
