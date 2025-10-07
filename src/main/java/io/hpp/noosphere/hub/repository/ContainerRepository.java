package io.hpp.noosphere.hub.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.hpp.noosphere.hub.domain.Container;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContainerRepository extends JpaRepository<Container, UUID>, ContainerRepositoryCustom {
}

interface ContainerRepositoryCustom {
    List<Container> findByName(String name);
    List<Container> findByCreatedByUserId(String userId);
}

@Repository
class ContainerRepositoryCustomImpl implements ContainerRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;

    public ContainerRepositoryCustomImpl(
      JPAQueryFactory jpaQueryFactory,
      EntityManager entityManager
    ){
        this.jpaQueryFactory = jpaQueryFactory;
        this.entityManager = entityManager;
    }

    @Override
    public List<Container> findByName(String name) {
//        QContainer qContainer = QContainer.container;
//        return queryFactory.selectFrom(qContainer)
//            .where(qContainer.name.eq(name))
//            .fetch();
        return null;
    }
//
    @Override
    public List<Container> findByCreatedByUserId(String userId) {
//        QContainer qContainer = QContainer.container;
//        return queryFactory.selectFrom(qContainer)
//            .where(qContainer.createdByUser.id.eq(userId))
//            .fetch();
        return null;
    }
}