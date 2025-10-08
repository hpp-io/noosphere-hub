package io.hpp.noosphere.hub.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.hpp.noosphere.hub.domain.AgentStatus;
import io.hpp.noosphere.hub.domain.QAgentStatus;
import io.hpp.noosphere.hub.domain.enumeration.StatusCode;
import io.hpp.noosphere.hub.service.uil.CommonUtils;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AgentStatus entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AgentStatusRepository extends JpaRepository<AgentStatus, UUID>, AgentStatusRepositoryCustom {

}

interface AgentStatusRepositoryCustom {

  Page<AgentStatus> search(String agentName, StatusCode agentStatusCode, Pageable pageable);

  Page<AgentStatus> findActiveByAgentName(String agentName, Pageable pageable);

  Optional<AgentStatus> findByAgentId(UUID agentId);

  Page<AgentStatus> findByAgentStatusCode(StatusCode agentStatusCode, Pageable pageable);
}

@Repository
class AgentStatusRepositoryCustomImpl implements AgentStatusRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;
  private final EntityManager entityManager;

  public AgentStatusRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory, EntityManager entityManager) {
    this.jpaQueryFactory = jpaQueryFactory;
    this.entityManager = entityManager;
  }

  @Override
  public Page<AgentStatus> search(String agentName, StatusCode agentStatusCode, Pageable pageable) {
    QAgentStatus qAgentStatus = QAgentStatus.agentStatus;
    BooleanBuilder builder = new BooleanBuilder();
    if (agentStatusCode != null) {
      builder.and(qAgentStatus.agent.statusCode.eq(agentStatusCode));
    }
    if (CommonUtils.isValid(agentName)) {
      builder.and(qAgentStatus.agent.name.containsIgnoreCase(agentName));
    }
    if (builder.hasValue()) {
      JPQLQuery<AgentStatus> query = jpaQueryFactory.selectFrom(qAgentStatus).where(builder);
      return QuerydslUtil.fetchPage(query, pageable);
    } else {
      return Page.empty();
    }
  }

  @Override
  public Page<AgentStatus> findActiveByAgentName(String agentName, Pageable pageable) {
    return this.search(agentName, StatusCode.ACTIVE, pageable);
  }

  @Override
  public Page<AgentStatus> findByAgentStatusCode(StatusCode agentStatusCode, Pageable pageable) {
    return this.search(null, agentStatusCode, pageable);
  }

  @Override
  public Optional<AgentStatus> findByAgentId(UUID agentId) {
    QAgentStatus qAgentStatus = QAgentStatus.agentStatus;
    BooleanBuilder builder = new BooleanBuilder();
    builder.and(qAgentStatus.agent.id.eq(agentId));
    JPQLQuery<AgentStatus> query = jpaQueryFactory.selectFrom(qAgentStatus).where(builder);
    return Optional.ofNullable(query.fetchOne());
  }
}
