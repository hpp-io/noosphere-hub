package io.hpp.noosphere.hub.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.hpp.noosphere.hub.domain.Container;
import io.hpp.noosphere.hub.domain.QContainer;
import io.hpp.noosphere.hub.domain.enumeration.StatusCode;
import io.hpp.noosphere.hub.service.uil.CommonUtils;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContainerRepository extends JpaRepository<Container, UUID>, ContainerRepositoryCustom {

}

interface ContainerRepositoryCustom {

  Page<Container> search(String searchText, String name, StatusCode statusCode, String createdByUserId, String walletAddress, Pageable pageable);

  Page<Container> findActiveByName(String name, Pageable pageable);

  Page<Container> findActiveByCreatedByUserId(String userId, Pageable pageable);
}

@Repository
class ContainerRepositoryCustomImpl implements ContainerRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;
  private final EntityManager entityManager;

  public ContainerRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory, EntityManager entityManager) {
    this.jpaQueryFactory = jpaQueryFactory;
    this.entityManager = entityManager;
  }

  @Override
  public Page<Container> search(String searchText, String name, StatusCode statusCode, String createdByUserId, String walletAddress, Pageable pageable) {
    QContainer qContainer = QContainer.container;
    BooleanBuilder builder = new BooleanBuilder();
    if (statusCode != null) {
      builder.and(qContainer.statusCode.eq(statusCode));
    }
    if (CommonUtils.isValid(name)) {
      builder.and(qContainer.name.containsIgnoreCase(name));
    }
    if (CommonUtils.isValid(createdByUserId)) {
      builder.and(qContainer.createdByUser.id.eq(createdByUserId));
    }
    if (CommonUtils.isValid(walletAddress)) {
      builder.and(qContainer.walletAddress.eq(walletAddress));
    }
    if (CommonUtils.isValid(searchText)) {
      BooleanBuilder searchBuilder = new BooleanBuilder();
      searchBuilder.or(qContainer.name.containsIgnoreCase(searchText));
      searchBuilder.or(qContainer.imageName.containsIgnoreCase(searchText));
      builder.and(searchBuilder);
    }
    if (builder.hasValue()) {
      JPQLQuery<Container> query = jpaQueryFactory.selectFrom(qContainer).where(builder);
      return QuerydslUtil.fetchPage(query, pageable);
    } else {
      return Page.empty();
    }
  }

  @Override
  public Page<Container> findActiveByName(String name, Pageable pageable) {
    return this.search(null, name, StatusCode.ACTIVE, null, null, pageable);
  }

  @Override
  public Page<Container> findActiveByCreatedByUserId(String userId, Pageable pageable) {
    return this.search(null, null, StatusCode.ACTIVE, userId, null, pageable);
  }
}
