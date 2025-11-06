package io.hpp.noosphere.hub.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.hpp.noosphere.hub.domain.Validator;
import io.hpp.noosphere.hub.domain.QValidator;
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
 * Spring Data JPA repository for the Validator entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ValidatorRepository extends JpaRepository<Validator, UUID>, ValidatorRepositoryCustom {

}

interface ValidatorRepositoryCustom {

    Page<Validator> search(String name, StatusCode statusCode, String createdByUserId, String walletAddress, Pageable pageable);

    Page<Validator> findActiveByName(String name, Pageable pageable);

    Page<Validator> findActiveByCreatedByUserId(String userId, Pageable pageable);

    Optional<Validator> findByIdAndCreatedByUserId(UUID id, String createdByUserId);

}

@Repository
class ValidatorRepositoryCustomImpl implements ValidatorRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;

    public ValidatorRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory, EntityManager entityManager) {
        this.jpaQueryFactory = jpaQueryFactory;
        this.entityManager = entityManager;
    }

    @Override
    public Page<Validator> search(String name, StatusCode statusCode, String createdByUserId, String walletAddress, Pageable pageable) {
        QValidator qValidator = QValidator.validator;
        BooleanBuilder builder = new BooleanBuilder();
        if (statusCode != null) {
            builder.and(qValidator.statusCode.eq(statusCode));
        }
        if (CommonUtils.isValid(name)) {
            builder.and(qValidator.name.containsIgnoreCase(name));
        }
        if (CommonUtils.isValid(createdByUserId)) {
            builder.and(qValidator.createdByUser.id.eq(createdByUserId));
        }
        if (CommonUtils.isValid(walletAddress)) {
            builder.and(qValidator.walletAddress.eq(walletAddress));
        }
        if (builder.hasValue()) {
            JPQLQuery<Validator> query = jpaQueryFactory.selectFrom(qValidator).where(builder);
            return QuerydslUtil.fetchPage(query, pageable);
        } else {
            return Page.empty();
        }
    }

    @Override
    public Page<Validator> findActiveByName(String name, Pageable pageable) {
        return this.search(name, StatusCode.ACTIVE, null, null, pageable);
    }

    @Override
    public Page<Validator> findActiveByCreatedByUserId(String userId, Pageable pageable) {
        return this.search(null, StatusCode.ACTIVE, userId, null, pageable);
    }

    @Override
    public Optional<Validator> findByIdAndCreatedByUserId(UUID id, String createdByUserId) {
        QValidator qValidator = QValidator.validator;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qValidator.id.eq(id));
        builder.and(qValidator.createdByUser.id.eq(createdByUserId));
        JPQLQuery<Validator> query = jpaQueryFactory.selectFrom(qValidator).where(builder);
        return Optional.ofNullable(query.fetchOne());
    }

}
