package io.hpp.noosphere.hub.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
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
 * A DTO for the {@link io.hpp.noosphere.hub.domain.AgentStatus} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentStatusDTO implements Serializable {

    @NotNull
    private UUID id;

    @NotNull
    private Instant createdAt;

    private Instant lastKeepAliveAt;

    private AgentDTO agent;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof AgentStatusDTO that)) {
            return false;
        }

      return new EqualsBuilder().append(id, that.id)
          .append(agent, that.agent).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(agent).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
          .append("id", id)
          .append("createdAt", createdAt)
          .append("lastKeepAliveAt", lastKeepAliveAt)
          .append("agent", agent !=null ? agent.getId() : null)
          .toString();
    }
}
