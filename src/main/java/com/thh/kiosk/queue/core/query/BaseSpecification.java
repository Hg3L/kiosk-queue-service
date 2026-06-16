package com.thh.kiosk.queue.core.query;

import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BaseSpecification<T> implements Specification<T> {

    private SearchCriteria searchCriteria;

    @Override
    public @Nullable Predicate toPredicate(
            @NonNull Root<T> root,
            @Nullable CriteriaQuery<?> query,
            @NonNull CriteriaBuilder criteriaBuilder) {

        switch (searchCriteria.operation()) {
            case EQUALITY -> {
                return criteriaBuilder.equal(
                        root.get(searchCriteria.key()),
                        searchCriteria.value()
                );
            }
            case NEGATION -> {
                return criteriaBuilder.notEqual(
                        root.get(searchCriteria.key()),
                        searchCriteria.value()
                );
            }
            case GREATER_THAN -> {
                return criteriaBuilder.greaterThan(
                        root.get(searchCriteria.key()),
                        searchCriteria.value().toString()
                );
            }
            case LESS_THAN -> {
                return criteriaBuilder.lessThan(
                        root.get(searchCriteria.key()),
                        searchCriteria.value().toString()
                );
            }
            case LIKE -> {
                return criteriaBuilder.like(
                        root.get(searchCriteria.key()),
                        "%" + searchCriteria.value() + "%"
                );
            }
            case IN -> {
                return root.get(searchCriteria.key()).in(searchCriteria.value());
            }
            case NOT_IN -> {
                return criteriaBuilder.not(root.get(searchCriteria.key()).in(searchCriteria.value()));
            }
            case STARTS_WITH -> {
                return criteriaBuilder.like(
                        root.get(searchCriteria.key()),
                        searchCriteria.value() + "%"
                );
            }
            case ENDS_WITH -> {
                return criteriaBuilder.like(
                        root.get(searchCriteria.key()),
                        "%" + searchCriteria.value()
                );
            }
            default -> {
                return null;
            }
        }
    }
}
