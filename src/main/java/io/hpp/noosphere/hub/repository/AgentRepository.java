package io.hpp.noosphere.hub.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.hpp.noosphere.hub.domain.Agent;
import io.hpp.noosphere.hub.domain.QAgent;
import io.hpp.noosphere.hub.domain.enumeration.StatusCode;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Agent entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AgentRepository extends JpaRepository<Agent, UUID>, AgentRepositoryCustom {
}

interface AgentRepositoryCustom {
  List<Agent> findByName(StatusCode statusCode, String name);
  List<Agent> findActiveByName(String name);
  List<Agent> findByCreatedByUserId(String userId);
}

@Repository
class AgentRepositoryCustomImpl implements AgentRepositoryCustom {
  private final JPAQueryFactory jpaQueryFactory;
  private final EntityManager entityManager;

  public AgentRepositoryCustomImpl(
    JPAQueryFactory jpaQueryFactory,
    EntityManager entityManager
  ){
    this.jpaQueryFactory = jpaQueryFactory;
    this.entityManager = entityManager;
  }

  @Override
  public List<Agent> findByName(StatusCode statusCode, String name) {
    QAgent qAgent = QAgent.agent;
    return jpaQueryFactory.selectFrom(qAgent)
      .where(qAgent.name.eq(name)
      .and(qAgent.statusCode.eq(statusCode)))
      .fetch();
  }

  @Override
  public List<Agent> findActiveByName(String name) {
    return this.findByName(StatusCode.ACTIVE, name);
  }

  @Override
  public List<Agent> findByCreatedByUserId(String userId) {
    QAgent qAgent = QAgent.agent;
    return jpaQueryFactory.selectFrom(qAgent)
      .where(qAgent.createdByUser.id.eq(userId))
      .fetch();
  }
}
