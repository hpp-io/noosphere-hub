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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
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

@Entity
@Table(name = "agent_req")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@RequiredArgsConstructor
@Getter
@Setter
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AgentRequest implements Serializable {

  private static final long serialVersionUID = 1L;

  @NotNull
  @Id
  @GeneratedValue
  @JdbcTypeCode(SqlTypes.VARCHAR)
  @Column(name = "id", length = 36, nullable = false)
  private UUID id;

  @NotNull
  @Column(name = "status_code", length = 20, nullable = false)
  @Enumerated(EnumType.STRING)
  private StatusCode statusCode;

  @NotNull
  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at")
  private Instant updatedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JsonIgnoreProperties(value = { "createdByUser", "updatedByUser", "agentContainers", "agentStatus" }, allowSetters = true)
  private Agent agent;

  @ManyToOne(fetch = FetchType.LAZY)
  @JsonIgnoreProperties(value = { "createdByUser", "updatedByUser", "agentContainers" }, allowSetters = true)
  private Container container;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_subscr_id")
  @JsonIgnoreProperties(value = { "owner", "container" }, allowSetters = true)
  private UserSubscription userSubscription;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof AgentRequest that)) {
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
