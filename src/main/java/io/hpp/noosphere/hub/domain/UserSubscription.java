package io.hpp.noosphere.hub.domain;

import io.hpp.noosphere.hub.domain.enumeration.PeriodType;
import io.hpp.noosphere.hub.domain.enumeration.StatusCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
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
@Table(name = "user_subscr")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@RequiredArgsConstructor
@Getter
@Setter
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserSubscription implements Serializable {

  private static final long serialVersionUID = 1L;

  @NotNull
  @Id
  @GeneratedValue
  @JdbcTypeCode(SqlTypes.VARCHAR)
  @Column(name = "id", length = 36, nullable = false)
  private UUID id;

  @Column(name = "amount", precision = 21, scale = 2)
  private BigDecimal amount;

  @NotNull
  @Column(name = "period_type", length = 20, nullable = false)
  @Enumerated(EnumType.STRING)
  private PeriodType periodType;

  @NotNull
  @Column(name = "period_value")
  private Integer periodValue;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(unique = true)
  private User owner;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(unique = true)
  private Container container;

  @NotNull
  @Column(name = "status_code", length = 20, nullable = false)
  @Enumerated(EnumType.STRING)
  private StatusCode statusCode;

  @NotNull
  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at")
  private Instant updatedAt;


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof UserSubscription that)) {
      return false;
    }

    return new EqualsBuilder().append(id, that.id).append(amount, that.amount).append(periodType, that.periodType)
      .append(periodValue, that.periodValue).append(owner, that.owner).append(container, that.container).append(statusCode, that.statusCode)
      .append(createdAt, that.createdAt).append(updatedAt, that.updatedAt).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(id).append(amount).append(periodType).append(periodValue).append(owner).append(container).append(statusCode)
      .append(createdAt).append(updatedAt).toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
      .append("id", id)
      .append("amount", amount)
      .append("periodType", periodType)
      .append("periodValue", periodValue)
      .append("owner", owner != null ? owner.getId() : null)
      .append("container", container != null ? container.getId() : null)
      .append("statusCode", statusCode)
      .append("createdAt", createdAt)
      .append("updatedAt", updatedAt)
      .toString();
  }
}
