package io.hpp.noosphere.hub.service.dto;

import com.fasterxml.jackson.annotation.JsonView;
import io.hpp.noosphere.hub.domain.enumeration.StatusCode;
import io.swagger.v3.oas.annotations.media.Schema;
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

@SuppressWarnings("common-java:DuplicatedBlocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema
public class AgentRequestDTO implements Serializable {

  @JsonView(JsonViewType.Shallow.class)
  private UUID id;

  @NotNull
  @JsonView(JsonViewType.Full.class)
  private StatusCode statusCode;

  @JsonView(JsonViewType.Full.class)
  private Instant createdAt;

  @JsonView(JsonViewType.Full.class)
  private Instant updatedAt;

  @JsonView(JsonViewType.Shallow.class)
  private AgentDTO agent;

  @JsonView(JsonViewType.Shallow.class)
  private ContainerDTO container;

  @JsonView(JsonViewType.Shallow.class)
  private UserSubscriptionDTO userSubscription;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof AgentRequestDTO that)) {
      return false;
    }

    return new EqualsBuilder()
      .append(id, that.id)
      .append(agent, that.agent)
      .append(container, that.container)
      .append(userSubscription, that.userSubscription)
      .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(id).append(agent).append(container).append(userSubscription).toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
      .append("id", id)
      .append("statusCode", statusCode)
      .append("createdAt", createdAt)
      .append("updatedAt", updatedAt)
      .append("agent", agent != null ? agent.getId() : null)
      .append("container", container != null ? container.getId() : null)
      .append("userSubscription", userSubscription != null ? userSubscription.getId() : null)
      .toString();
  }
}
