package io.hpp.noosphere.hub.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.hpp.noosphere.common.domain.enumeration.StatusCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
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
@RequiredArgsConstructor
@Getter
@Setter
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Container implements Serializable {

  private static final long serialVersionUID = 1L;

  @NotNull
  @Id
  @GeneratedValue
  @JdbcTypeCode(SqlTypes.VARCHAR)
  @Column(name = "id", length = 36, nullable = false)
  private UUID id;

  @NotNull
  @Column(name = "name")
  private String name;

  @NotNull
  @Column(name = "image_name", nullable = false)
  private String imageName;

  @NotNull
  @Column(name = "port", nullable = false)
  private Integer port;

  @Column(name = "command", length = 1024)
  private String command;

  @Lob
  @Column(name = "env")
  private String environmentVariables;

  @Lob
  @Column(name = "volumes")
  private String volumes;

  @Lob
  @Column(name = "payments")
  private String payments;

  @NotNull
  @Column(name = "status_code", length = 20, nullable = false)
  @Enumerated(EnumType.STRING)
  private StatusCode statusCode;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof Container container)) {
      return false;
    }

    return new EqualsBuilder()
      .append(id, container.id)
      .append(name, container.name)
      .append(imageName, container.imageName)
      .append(port, container.port)
      .append(statusCode, container.statusCode)
      .append(createdAt, container.createdAt)
      .append(updatedAt, container.updatedAt)
      .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
      .append(id)
      .append(name)
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
      .append("agentContainers", agentContainers != null ? agentContainers.size() : null)
      .toString();
  }
}
