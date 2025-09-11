package io.hpp.noosphere.hub.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A NodeStatus.
 */
@Entity
@Table(name = "node_status")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NodeStatus implements Serializable {

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

    @JsonIgnoreProperties(value = { "createdByUser", "updatedByUser", "nodeContainers", "nodeStatus" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Node node;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public UUID getId() {
        return this.id;
    }

    public NodeStatus id(UUID id) {
        this.setId(id);
        return this;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public NodeStatus createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getLastKeepAliveAt() {
        return this.lastKeepAliveAt;
    }

    public NodeStatus lastKeepAliveAt(Instant lastKeepAliveAt) {
        this.setLastKeepAliveAt(lastKeepAliveAt);
        return this;
    }

    public void setLastKeepAliveAt(Instant lastKeepAliveAt) {
        this.lastKeepAliveAt = lastKeepAliveAt;
    }

    public Node getNode() {
        return this.node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public NodeStatus node(Node node) {
        this.setNode(node);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NodeStatus)) {
            return false;
        }
        return getId() != null && getId().equals(((NodeStatus) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NodeStatus{" +
            "id=" + getId() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", lastKeepAliveAt='" + getLastKeepAliveAt() + "'" +
            "}";
    }
}
