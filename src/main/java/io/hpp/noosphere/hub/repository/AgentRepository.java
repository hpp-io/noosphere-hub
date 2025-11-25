package io.hpp.noosphere.hub.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.hpp.noosphere.common.domain.enumeration.StatusCode;
import io.hpp.noosphere.common.repository.QuerydslUtil;
import io.hpp.noosphere.common.service.util.CommonUtils;
import io.hpp.noosphere.hub.domain.Agent;
import io.hpp.noosphere.hub.domain.QAgent;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Agent entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AgentRepository extends JpaRepository<Agent, UUID>, AgentRepositoryCustom {}

interface AgentRepositoryCustom {
  Page<Agent> search(
    String searchText,
    String name,
    StatusCode statusCode,
    String createdByUserId,
    String walletAddress,
    Pageable pageable
  );

  Page<Agent> findActiveByName(String name, Pageable pageable);

  Page<Agent> findActiveByCreatedByUserId(String userId, Pageable pageable);

  Optional<Agent> findByIdAndCreatedByUserId(UUID id, String createdByUserId);
}

@Repository
class AgentRepositoryCustomImpl implements AgentRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;
  private final EntityManager entityManager;

  public AgentRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory, EntityManager entityManager) {
    this.jpaQueryFactory = jpaQueryFactory;
    this.entityManager = entityManager;
  }

  @Override
  public Page<Agent> search(
    String searchText,
    String name,
    StatusCode statusCode,
    String createdByUserId,
    String walletAddress,
    Pageable pageable
  ) {
    QAgent qAgent = QAgent.agent;
    BooleanBuilder builder = new BooleanBuilder();
    if (statusCode != null) {
      builder.and(qAgent.statusCode.eq(statusCode));
    }
    if (CommonUtils.isValid(name)) {
      builder.and(qAgent.name.containsIgnoreCase(name));
    }
    if (CommonUtils.isValid(createdByUserId)) {
      builder.and(qAgent.createdByUser.id.eq(createdByUserId));
    }
    if (CommonUtils.isValid(walletAddress)) {
      builder.and(qAgent.walletAddress.eq(walletAddress));
    }
    if (CommonUtils.isValid(searchText)) {
      BooleanBuilder searchBuilder = new BooleanBuilder();
      searchBuilder.or(qAgent.name.containsIgnoreCase(searchText));
      searchBuilder.or(qAgent.description.containsIgnoreCase(searchText));
      builder.and(searchBuilder);
    }
    if (builder.hasValue()) {
      JPQLQuery<Agent> query = jpaQueryFactory.selectFrom(qAgent).where(builder);
      return QuerydslUtil.fetchPage(query, pageable);
    } else {
      return Page.empty();
    }
  }

  @Override
  public Page<Agent> findActiveByName(String name, Pageable pageable) {
    return this.search(null, name, StatusCode.ACTIVE, null, null, pageable);
  }

  @Override
  public Page<Agent> findActiveByCreatedByUserId(String userId, Pageable pageable) {
    return this.search(null, null, StatusCode.ACTIVE, userId, null, pageable);
  }

  @Override
  public Optional<Agent> findByIdAndCreatedByUserId(UUID id, String createdByUserId) {
    QAgent qAgent = QAgent.agent;
    BooleanBuilder builder = new BooleanBuilder();
    builder.and(qAgent.id.eq(id));
    builder.and(qAgent.createdByUser.id.eq(createdByUserId));
    JPQLQuery<Agent> query = jpaQueryFactory.selectFrom(qAgent).where(builder);
    return Optional.ofNullable(query.fetchOne());
  }
}
