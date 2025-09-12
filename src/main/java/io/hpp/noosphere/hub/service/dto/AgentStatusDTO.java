package io.hpp.noosphere.hub.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link io.hpp.noosphere.hub.domain.AgentStatus} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AgentStatusDTO implements Serializable {

    @NotNull
    private UUID id;

    @NotNull
    private Instant createdAt;

    private Instant lastKeepAliveAt;

    private AgentDTO agent;

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

    public AgentDTO getAgent() {
        return agent;
    }

    public void setAgent(AgentDTO agent) {
        this.agent = agent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AgentStatusDTO)) {
            return false;
        }

        AgentStatusDTO agentStatusDTO = (AgentStatusDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, agentStatusDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AgentStatusDTO{" +
            "id='" + getId() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", lastKeepAliveAt='" + getLastKeepAliveAt() + "'" +
            ", agent=" + getAgent() +
            "}";
    }
}
