package io.hpp.noosphere.hub.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.hpp.noosphere.hub.domain.Agent;
import io.hpp.noosphere.hub.domain.AgentContainer;
import io.hpp.noosphere.hub.domain.QAgentContainer;
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
 * Spring Data JPA repository for the AgentContainer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AgentContainerRepository extends JpaRepository<AgentContainer, UUID>, AgentContainerRepositoryCustom {}

interface AgentContainerRepositoryCustom {
    Page<AgentContainer> search(UUID agentId, String containerName, StatusCode statusCode, Pageable pageable);

    Page<AgentContainer> findActiveByContainerName(UUID agentId, String containerName, Pageable pageable);
    Optional<AgentContainer> findByAgentIdAndContainerId(UUID agentId, UUID containerId);
}

@Repository
class AgentContainerRepositoryCustomImpl implements AgentContainerRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;

    public AgentContainerRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory, EntityManager entityManager) {
        this.jpaQueryFactory = jpaQueryFactory;
        this.entityManager = entityManager;
    }

    @Override
    public Page<AgentContainer> search(UUID agentId, String containerName, StatusCode statusCode, Pageable pageable) {
        QAgentContainer qAgentContainer = QAgentContainer.agentContainer;
        BooleanBuilder builder = new BooleanBuilder();
        if (agentId != null) {
            builder.and(qAgentContainer.agent.id.eq(agentId));
        }
        if (statusCode != null) {
            builder.and(qAgentContainer.statusCode.eq(statusCode));
        }
        if (CommonUtils.isValid(containerName)) {
            builder.and(qAgentContainer.container.name.containsIgnoreCase(containerName));
        }
        if (builder.hasValue()) {
            JPQLQuery<AgentContainer> query = jpaQueryFactory.selectFrom(qAgentContainer).where(builder);
            return QuerydslUtil.fetchPage(query, pageable);
        } else {
            return Page.empty();
        }
    }

    @Override
    public Page<AgentContainer> findActiveByContainerName(UUID agentId, String containerName, Pageable pageable) {
        return this.search(agentId, containerName, StatusCode.ACTIVE, pageable);
    }

    @Override
    public Optional<AgentContainer> findByAgentIdAndContainerId(UUID agentId, UUID containerId) {
        QAgentContainer qAgentContainer = QAgentContainer.agentContainer;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qAgentContainer.agent.id.eq(agentId));
        builder.and(qAgentContainer.container.id.eq(containerId));
        JPQLQuery<AgentContainer> query = jpaQueryFactory.selectFrom(qAgentContainer).where(builder);
        return Optional.ofNullable(query.fetchOne());
    }
}
