package io.hpp.noosphere.hub.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.hpp.noosphere.hub.domain.QUserSubscription;
import io.hpp.noosphere.hub.domain.UserSubscription;
import io.hpp.noosphere.hub.domain.enumeration.StatusCode;
import io.hpp.noosphere.hub.service.uil.CommonUtils;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UserSubscription entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, UUID>, UserSubscriptionRepositoryCustom {

}

interface UserSubscriptionRepositoryCustom {

    Page<UserSubscription> search(String userId, List<UUID> containerIdList, StatusCode statusCode, String containerName, Pageable pageable);

    Long countAllUserSubscriptionIdListByContainerIdList(List<UUID> containerIdList,  StatusCode statusCode);

    Optional<UserSubscription> findActiveByByUserIdAndContainerId(String userId, UUID containerId);


}

@Repository
class UserSubscriptionRepositoryCustomImpl implements UserSubscriptionRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;

    public UserSubscriptionRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory, EntityManager entityManager) {
        this.jpaQueryFactory = jpaQueryFactory;
        this.entityManager = entityManager;
    }

    @Override
    public Page<UserSubscription> search(String userId, List<UUID> containerIdList, StatusCode statusCode, String containerName, Pageable pageable) {
        QUserSubscription qUserSubscription = QUserSubscription.userSubscription;
        BooleanBuilder builder = new BooleanBuilder();
        if (CommonUtils.isValid(userId)) {
            builder.and(qUserSubscription.owner.id.eq(userId));
        }
        if (containerIdList != null && !containerIdList.isEmpty() ) {
            builder.and(qUserSubscription.container.id.in(containerIdList));
        }
        if (statusCode != null) {
            builder.and(qUserSubscription.statusCode.eq(statusCode));
        }
        if (CommonUtils.isValid(containerName)) {
            builder.and(qUserSubscription.container.name.containsIgnoreCase(containerName));
        }
        if (builder.hasValue()) {
            JPQLQuery<UserSubscription> query = jpaQueryFactory.selectFrom(qUserSubscription).where(builder);
            return QuerydslUtil.fetchPage(query, pageable);
        } else {
            return Page.empty();
        }
    }

    @Override
    public Long countAllUserSubscriptionIdListByContainerIdList(List<UUID> containerIdList,  StatusCode statusCode){
        QUserSubscription qUserSubscription = QUserSubscription.userSubscription;
        BooleanBuilder builder = new BooleanBuilder();
        if (containerIdList != null && !containerIdList.isEmpty() ) {
            builder.and(qUserSubscription.container.id.in(containerIdList));
        }
        if (statusCode != null) {
            builder.and(qUserSubscription.statusCode.eq(statusCode));
        }
        return jpaQueryFactory.select(qUserSubscription.id.countDistinct())
          .from(qUserSubscription)
          .where(builder)
          .fetchOne();

    }

    @Override
    public Optional<UserSubscription> findActiveByByUserIdAndContainerId(String userId, UUID containerId) {
        QUserSubscription qUserSubscription = QUserSubscription.userSubscription;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUserSubscription.owner.id.eq(userId));
        builder.and(qUserSubscription.container.id.eq(containerId));
        JPQLQuery<UserSubscription> query = jpaQueryFactory.selectFrom(qUserSubscription).where(builder);
        return Optional.ofNullable(query.fetchOne());
    }


}
