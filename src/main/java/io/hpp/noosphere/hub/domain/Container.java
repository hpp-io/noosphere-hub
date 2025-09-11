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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A Container.
 */
@Entity
@Table(name = "container")
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
    @JsonIgnoreProperties(value = { "node", "container" }, allowSetters = true)
    private Set<NodeContainer> nodeContainers = new HashSet<>();

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

    public Set<NodeContainer> getNodeContainers() {
        return this.nodeContainers;
    }

    public void setNodeContainers(Set<NodeContainer> nodeContainers) {
        if (this.nodeContainers != null) {
            this.nodeContainers.forEach(i -> i.setContainer(null));
        }
        if (nodeContainers != null) {
            nodeContainers.forEach(i -> i.setContainer(this));
        }
        this.nodeContainers = nodeContainers;
    }

    public Container nodeContainers(Set<NodeContainer> nodeContainers) {
        this.setNodeContainers(nodeContainers);
        return this;
    }

    public Container addNodeContainer(NodeContainer nodeContainer) {
        this.nodeContainers.add(nodeContainer);
        nodeContainer.setContainer(this);
        return this;
    }

    public Container removeNodeContainer(NodeContainer nodeContainer) {
        this.nodeContainers.remove(nodeContainer);
        nodeContainer.setContainer(null);
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
