package com.iot.query.infrastructure.adapter.out.persistence.repository;

import com.iot.query.infrastructure.adapter.out.persistence.AggregationFunction;
import com.iot.query.infrastructure.adapter.out.persistence.model.ReadingEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;

public class ReadingNativeAggregateRepositoryImpl implements ReadingNativeAggregateRepository {

  public static final String VALUE = "value";
  @PersistenceContext EntityManager entityManager;

  private Optional<Double> findAggregate(
      AggregationFunction<ReadingEntity, Double> aggregateFunction,
      Specification<ReadingEntity> specification) {
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Double> criteriaQuery = criteriaBuilder.createQuery(Double.class);
    Root<ReadingEntity> readingEntityRoot = criteriaQuery.from(ReadingEntity.class);

    criteriaQuery.select(aggregateFunction.apply(criteriaBuilder, readingEntityRoot));

    if (specification != null) {
      criteriaQuery.where(
          specification.toPredicate(readingEntityRoot, criteriaQuery, criteriaBuilder));
    }

    Double result = entityManager.createQuery(criteriaQuery).getSingleResult();
    return Optional.ofNullable(result);
  }

  @Override
  public Optional<Double> findAvgAggregateWithSpec(Specification<ReadingEntity> specification) {

    return findAggregate(
        (cb, readingCriteriaQuery) -> cb.avg(readingCriteriaQuery.get(VALUE)), specification);
  }

  @Override
  public Optional<Double> findMaxAggregateWithSpec(Specification<ReadingEntity> specification) {
    return findAggregate(
        (cb, readingCriteriaQuery) -> cb.max(readingCriteriaQuery.get(VALUE)), specification);
  }

  @Override
  public Optional<Double> findMinAggregateWithSpec(Specification<ReadingEntity> specification) {

    return findAggregate(
        (cb, readingCriteriaQuery) -> cb.min(readingCriteriaQuery.get(VALUE)), specification);
  }
}
