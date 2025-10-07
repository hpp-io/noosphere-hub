package io.hpp.noosphere.hub.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import java.util.Collections;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;

public class QuerydslUtil {

    public static <T> Page<T> fetchPage(JPQLQuery<T> query, Pageable pageable) {
        Assert.notNull(query, "Query must not be null");
        Assert.notNull(pageable, "Pageable must not be null");

        // sort 적용
        applySorting(query, pageable.getSort());

        if (pageable.isPaged()) {
            long total = query.fetchCount();

            if (total == 0) {
                return new PageImpl<>(Collections.emptyList(), pageable, total);
            }

            List<T> content = query.offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();
            return new PageImpl<>(content, pageable, total);
        } else {
            // Pageable.unpaged() 경우 페이지네이션을 사용하지 않음
            List<T> content = query.fetch();
            return new PageImpl<>(content, Pageable.unpaged(), content.size());
        }
    }

    public static <T> JPQLQuery<T> applySorting(JPQLQuery<T> query, Sort sort) {
        if (sort != null) {
            for (Sort.Order order : sort) {
                query = query.orderBy(
                    new OrderSpecifier<>(order.isAscending() ? Order.ASC : Order.DESC, Expressions.stringPath(order.getProperty()))
                );
            }
        }
        return query;
    }
}
