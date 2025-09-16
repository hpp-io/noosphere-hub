package io.hpp.noosphere.hub.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A Container.
 */
@Entity
@Table(name = "container")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Container implements Serializable {

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
    @Column(name = "wallet_address", nullable = false)
    private String walletAddress;

    @NotNull
    @Column(name = "price", precision = 21, scale = 2, nullable = false)
    private BigDecimal price;

    @NotNull
    @Size(max = 20)
    @Column(name = "status_code", length = 20, nullable = false)
    private String statusCode;

    @Lob
    @Column(name = "description")
    private String description;

    @Lob
    @Column(name = "parameters", nullable = false)
    private String parameters;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "container")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "node", "container" }, allowSetters = true)
    private Set<AgentContainer> agentContainers = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public UUID getId() {
        return this.id;
    }

    public Container id(UUID id) {
        this.setId(id);
        return this;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Container name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWalletAddress() {
        return this.walletAddress;
    }

    public Container walletAddress(String walletAddress) {
        this.setWalletAddress(walletAddress);
        return this;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public Container price(BigDecimal price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getStatusCode() {
        return this.statusCode;
    }

    public Container statusCode(String statusCode) {
        this.setStatusCode(statusCode);
        return this;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getDescription() {
        return this.description;
    }

    public Container description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParameters() {
        return this.parameters;
    }

    public Container parameters(String parameters) {
        this.setParameters(parameters);
        return this;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Container createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Container updatedAt(Instant updatedAt) {
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

    public Container createdByUser(User user) {
        this.setCreatedByUser(user);
        return this;
    }

    public User getUpdatedByUser() {
        return this.updatedByUser;
    }

    public void setUpdatedByUser(User user) {
        this.updatedByUser = user;
    }

    public Container updatedByUser(User user) {
        this.setUpdatedByUser(user);
        return this;
    }

    public Set<AgentContainer> getAgentContainers() {
        return this.agentContainers;
    }

    public void setAgentContainers(Set<AgentContainer> agentContainers) {
        if (this.agentContainers != null) {
            this.agentContainers.forEach(i -> i.setContainer(null));
        }
        if (agentContainers != null) {
            agentContainers.forEach(i -> i.setContainer(this));
        }
        this.agentContainers = agentContainers;
    }

    public Container agentContainers(Set<AgentContainer> agentContainers) {
        this.setAgentContainers(agentContainers);
        return this;
    }

    public Container addAgentContainer(AgentContainer agentContainer) {
        this.agentContainers.add(agentContainer);
        agentContainer.setContainer(this);
        return this;
    }

    public Container removeAgentContainer(AgentContainer agentContainer) {
        this.agentContainers.remove(agentContainer);
        agentContainer.setContainer(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Container)) {
            return false;
        }
        return getId() != null && getId().equals(((Container) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Container{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", walletAddress='" + getWalletAddress() + "'" +
            ", price=" + getPrice() +
            ", statusCode='" + getStatusCode() + "'" +
            ", description='" + getDescription() + "'" +
            ", parameters='" + getParameters() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
