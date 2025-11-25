package io.hpp.noosphere.hub.service.dto;

import com.fasterxml.jackson.annotation.JsonView;
import io.hpp.noosphere.common.domain.enumeration.StatusCode;
import io.hpp.noosphere.hub.domain.enumeration.PeriodType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@SuppressWarnings("common-java:DuplicatedBlocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema
public class UserSubscriptionDTO implements Serializable {

  @JsonView(JsonViewType.Shallow.class)
  private UUID id;

  @JsonView(JsonViewType.Update.class)
  private BigDecimal amount;

  @NotNull
  @JsonView(JsonViewType.Update.class)
  private PeriodType periodType;

  @NotNull
  @JsonView(JsonViewType.Update.class)
  private Integer periodValue;

  @JsonView(JsonViewType.Update.class)
  private Instant activatedAt;

  @JsonView(JsonViewType.Update.class)
  private Integer numberOfExecution;

  @NotNull
  @JsonView(JsonViewType.Update.class)
  private String walletAddress;

  @JsonView(JsonViewType.Update.class)
  private String verifierAddress;

  @JsonView(JsonViewType.Update.class)
  private String referenceId;

  @NotNull
  @JsonView(JsonViewType.Full.class)
  private StatusCode statusCode;

  @JsonView(JsonViewType.Full.class)
  private Instant createdAt;

  @JsonView(JsonViewType.Full.class)
  private Instant updatedAt;

  @JsonView(JsonViewType.Update.class)
  private UserDTO owner;

  @JsonView(JsonViewType.Update.class)
  private ContainerDTO container;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof UserSubscriptionDTO that)) {
      return false;
    }

    return new EqualsBuilder()
      .append(id, that.id)
      .append(amount, that.amount)
      .append(periodType, that.periodType)
      .append(referenceId, that.referenceId)
      .append(walletAddress, that.walletAddress)
      .append(periodValue, that.periodValue)
      .append(statusCode, that.statusCode)
      .append(createdAt, that.createdAt)
      .append(updatedAt, that.updatedAt)
      .append(owner, that.owner)
      .append(container, that.container)
      .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
      .append(id)
      .append(amount)
      .append(periodType)
      .append(periodValue)
      .append(referenceId)
      .append(walletAddress)
      .append(statusCode)
      .append(createdAt)
      .append(updatedAt)
      .append(owner)
      .append(container)
      .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
      .append("id", id)
      .append("referenceId", referenceId)
      .append("amount", amount)
      .append("periodType", periodType)
      .append("periodValue", periodValue)
      .append("activatedAt", activatedAt)
      .append("numberOfExecution", numberOfExecution)
      .append("walletAddress", walletAddress)
      .append("verifierAddress", verifierAddress)
      .append("statusCode", statusCode)
      .append("createdAt", createdAt)
      .append("updatedAt", updatedAt)
      .append("owner", owner != null ? owner.getId() : null)
      .append("container", container != null ? container.getId() : null)
      .toString();
  }
}
