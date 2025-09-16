package io.hpp.noosphere.hub.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A Agent.
 */
@Entity
@Table(name = "agent")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Agent implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "id", length = 36, nullable = false)
    private UUID id;

    @Column(name = "name")
    private String name;

    @NotNull
    @Size(max = 1024)
    @Column(name = "api_url", length = 1024, nullable = false)
    private String apiUrl;

    @NotNull
    @Column(name = "api_key", nullable = false)
    private String apiKey;

    @NotNull
    @Size(max = 20)
    @Column(name = "status_code", length = 20, nullable = false)
    private String statusCode;

    @Lob
    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private User createdByUser;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private User updatedByUser;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "node")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "node", "container" }, allowSetters = true)
    private Set<AgentContainer> agentContainers = new HashSet<>();

    @JsonIgnoreProperties(value = { "agent" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "agent")
    private AgentStatus agentStatus;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public UUID getId() {
        return this.id;
    }

    public Agent id(UUID id) {
        this.setId(id);
        return this;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Agent name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApiUrl() {
        return this.apiUrl;
    }

    public Agent apiUrl(String apiUrl) {
        this.setApiUrl(apiUrl);
        return this;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public Agent apiKey(String apiKey) {
        this.setApiKey(apiKey);
        return this;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getStatusCode() {
        return this.statusCode;
    }

    public Agent statusCode(String statusCode) {
        this.setStatusCode(statusCode);
        return this;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getDescription() {
        return this.description;
    }

    public Agent description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Agent createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Agent updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getCreatedByUser() {
        return this.createdByUser;
    }

    public void setCreatedByUser(User user) {
        this.createdByUser = user;
    }

    public Agent createdByUser(User user) {
        this.setCreatedByUser(user);
        return this;
    }

    public User getUpdatedByUser() {
        return this.updatedByUser;
    }

    public void setUpdatedByUser(User user) {
        this.updatedByUser = user;
    }

    public Agent updatedByUser(User user) {
        this.setUpdatedByUser(user);
        return this;
    }

    public Set<AgentContainer> getAgentContainers() {
        return this.agentContainers;
    }

    public void setAgentContainers(Set<AgentContainer> agentContainers) {
        if (this.agentContainers != null) {
            this.agentContainers.forEach(i -> i.setNode(null));
        }
        if (agentContainers != null) {
            agentContainers.forEach(i -> i.setNode(this));
        }
        this.agentContainers = agentContainers;
    }

    public Agent agentContainers(Set<AgentContainer> agentContainers) {
        this.setAgentContainers(agentContainers);
        return this;
    }

    public Agent addAgentContainer(AgentContainer agentContainer) {
        this.agentContainers.add(agentContainer);
        agentContainer.setNode(this);
        return this;
    }

    public Agent removeAgentContainer(AgentContainer agentContainer) {
        this.agentContainers.remove(agentContainer);
        agentContainer.setNode(null);
        return this;
    }

    public AgentStatus getAgentStatus() {
        return this.agentStatus;
    }

    public void setAgentStatus(AgentStatus agentStatus) {
        if (this.agentStatus != null) {
            this.agentStatus.setAgent(null);
        }
        if (agentStatus != null) {
            agentStatus.setAgent(this);
        }
        this.agentStatus = agentStatus;
    }

    public Agent agentStatus(AgentStatus agentStatus) {
        this.setAgentStatus(agentStatus);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Agent)) {
            return false;
        }
        return getId() != null && getId().equals(((Agent) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Agent{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", apiUrl='" + getApiUrl() + "'" +
            ", apiKey='" + getApiKey() + "'" +
            ", statusCode='" + getStatusCode() + "'" +
            ", description='" + getDescription() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
