package io.hpp.noosphere.hub.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A AgentStatus.
 */
@Entity
@Table(name = "agent_status")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AgentStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "id", length = 36, nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "last_keep_alive_at")
    private Instant lastKeepAliveAt;

    @JsonIgnoreProperties(value = { "createdByUser", "updatedByUser", "agentContainers", "agentStatus" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Agent agent;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public UUID getId() {
        return this.id;
    }

    public AgentStatus id(UUID id) {
        this.setId(id);
        return this;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public AgentStatus createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getLastKeepAliveAt() {
        return this.lastKeepAliveAt;
    }

    public AgentStatus lastKeepAliveAt(Instant lastKeepAliveAt) {
        this.setLastKeepAliveAt(lastKeepAliveAt);
        return this;
    }

    public void setLastKeepAliveAt(Instant lastKeepAliveAt) {
        this.lastKeepAliveAt = lastKeepAliveAt;
    }

    public Agent getAgent() {
        return this.agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public AgentStatus agent(Agent agent) {
        this.setAgent(agent);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AgentStatus)) {
            return false;
        }
        return getId() != null && getId().equals(((AgentStatus) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AgentStatus{" +
            "id=" + getId() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", lastKeepAliveAt='" + getLastKeepAliveAt() + "'" +
            "}";
    }
}
