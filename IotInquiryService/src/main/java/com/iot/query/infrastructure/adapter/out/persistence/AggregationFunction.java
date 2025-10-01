package com.iot.query.infrastructure.adapter.out.persistence;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

@FunctionalInterface
public interface AggregationFunction<T, R> {

  Expression<R> apply(CriteriaBuilder criteriaBuilder, Root<T> root);
}
