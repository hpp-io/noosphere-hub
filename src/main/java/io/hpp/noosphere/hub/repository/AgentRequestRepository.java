package io.hpp.noosphere.hub.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.hpp.noosphere.hub.domain.AgentRequest;
import io.hpp.noosphere.hub.domain.QAgentRequest;
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
 * Spring Data JPA repository for the AgentRequest entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AgentRequestRepository extends JpaRepository<AgentRequest, UUID>, AgentRequestRepositoryCustom {}

interface AgentRequestRepositoryCustom {
  Page<AgentRequest> search(UUID containerId, UUID agentId, String agentName, StatusCode statusCode, Pageable pageable);

  Page<AgentRequest> findActiveByAgentId(UUID agentId, Pageable pageable);

  Optional<AgentRequest> findByAgentIdAndContainerId(UUID agentId, UUID containerId);
}

@Repository
class AgentRequestRepositoryCustomImpl implements AgentRequestRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;
  private final EntityManager entityManager;

  public AgentRequestRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory, EntityManager entityManager) {
    this.jpaQueryFactory = jpaQueryFactory;
    this.entityManager = entityManager;
  }

  @Override
  public Page<AgentRequest> search(UUID containerId, UUID agentId, String agentName, StatusCode statusCode, Pageable pageable) {
    QAgentRequest qAgentRequest = QAgentRequest.agentRequest;
    BooleanBuilder builder = new BooleanBuilder();
    if (containerId != null) {
      builder.and(qAgentRequest.container.id.eq(containerId));
    }
    if (agentId != null) {
      builder.and(qAgentRequest.agent.id.eq(agentId));
    }
    if (statusCode != null) {
      builder.and(qAgentRequest.statusCode.eq(statusCode));
    }
    if (CommonUtils.isValid(agentName)) {
      builder.and(qAgentRequest.agent.name.containsIgnoreCase(agentName));
    }

    if (builder.hasValue()) {
      JPQLQuery<AgentRequest> query = jpaQueryFactory.selectFrom(qAgentRequest).where(builder);
      return QuerydslUtil.fetchPage(query, pageable);
    } else {
      return Page.empty();
    }
  }

  @Override
  public Page<AgentRequest> findActiveByAgentId(UUID agentId, Pageable pageable) {
    return this.search(null, agentId, null, null, pageable);
  }

  @Override
  public Optional<AgentRequest> findByAgentIdAndContainerId(UUID agentId, UUID containerId) {
    QAgentRequest qAgentRequest = QAgentRequest.agentRequest;
    BooleanBuilder builder = new BooleanBuilder();
    builder.and(qAgentRequest.agent.id.eq(agentId));
    builder.and(qAgentRequest.container.id.eq(containerId));
    JPQLQuery<AgentRequest> query = jpaQueryFactory.selectFrom(qAgentRequest).where(builder);
    return Optional.ofNullable(query.fetchOne());
  }
}
