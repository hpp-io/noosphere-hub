package io.hpp.noosphere.hub.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.hpp.noosphere.common.domain.enumeration.StatusCode;
import io.hpp.noosphere.common.repository.QuerydslUtil;
import io.hpp.noosphere.common.service.util.CommonUtils;
import io.hpp.noosphere.hub.domain.QVerifier;
import io.hpp.noosphere.hub.domain.Verifier;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Verifier entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VerifierRepository extends JpaRepository<Verifier, UUID>, VerifierRepositoryCustom {}

interface VerifierRepositoryCustom {
  Page<Verifier> search(
    String searchText,
    String name,
    StatusCode statusCode,
    String createdByUserId,
    String walletAddress,
    String verifierAddress,
    Pageable pageable
  );

  Page<Verifier> findActiveByName(String name, Pageable pageable);

  Page<Verifier> findActiveByCreatedByUserId(String userId, Pageable pageable);

  Optional<Verifier> findByIdAndCreatedByUserId(UUID id, String createdByUserId);
}

@Repository
class VerifierRepositoryCustomImpl implements VerifierRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;
  private final EntityManager entityManager;

  public VerifierRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory, EntityManager entityManager) {
    this.jpaQueryFactory = jpaQueryFactory;
    this.entityManager = entityManager;
  }

  @Override
  public Page<Verifier> search(
    String searchText,
    String name,
    StatusCode statusCode,
    String createdByUserId,
    String walletAddress,
    String verifierAddress,
    Pageable pageable
  ) {
    QVerifier qVerifier = QVerifier.verifier;
    BooleanBuilder builder = new BooleanBuilder();
    if (statusCode != null) {
      builder.and(qVerifier.statusCode.eq(statusCode));
    }
    if (CommonUtils.isValid(name)) {
      builder.and(qVerifier.name.containsIgnoreCase(name));
    }
    if (CommonUtils.isValid(createdByUserId)) {
      builder.and(qVerifier.createdByUser.id.eq(createdByUserId));
    }
    if (CommonUtils.isValid(walletAddress)) {
      builder.and(qVerifier.walletAddress.eq(walletAddress));
    }
    if (CommonUtils.isValid(verifierAddress)) {
      builder.and(qVerifier.verifierAddress.eq(verifierAddress));
    }
    if (CommonUtils.isValid(searchText)) {
      BooleanBuilder searchBuilder = new BooleanBuilder();
      searchBuilder.or(qVerifier.name.containsIgnoreCase(searchText));
      searchBuilder.or(qVerifier.imageName.containsIgnoreCase(searchText));
      builder.and(searchBuilder);
    }
    if (builder.hasValue()) {
      JPQLQuery<Verifier> query = jpaQueryFactory.selectFrom(qVerifier).where(builder);
      return QuerydslUtil.fetchPage(query, pageable);
    } else {
      return Page.empty();
    }
  }

  @Override
  public Page<Verifier> findActiveByName(String name, Pageable pageable) {
    return this.search(null, name, StatusCode.ACTIVE, null, null, null, pageable);
  }

  @Override
  public Page<Verifier> findActiveByCreatedByUserId(String userId, Pageable pageable) {
    return this.search(null, null, StatusCode.ACTIVE, userId, null, null, pageable);
  }

  @Override
  public Optional<Verifier> findByIdAndCreatedByUserId(UUID id, String createdByUserId) {
    QVerifier qVerifier = QVerifier.verifier;
    BooleanBuilder builder = new BooleanBuilder();
    builder.and(qVerifier.id.eq(id));
    builder.and(qVerifier.createdByUser.id.eq(createdByUserId));
    JPQLQuery<Verifier> query = jpaQueryFactory.selectFrom(qVerifier).where(builder);
    return Optional.ofNullable(query.fetchOne());
  }
}
