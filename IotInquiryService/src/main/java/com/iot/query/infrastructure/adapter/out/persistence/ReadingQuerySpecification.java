package com.iot.query.infrastructure.adapter.out.persistence;

import com.iot.query.infrastructure.adapter.out.persistence.model.ReadingEntity;
import jakarta.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public final class ReadingQuerySpecification {
  private ReadingQuerySpecification() {}

  public static Specification<ReadingEntity> isBetweenTimeStamp(Instant from, Instant to) {
    return ((root, query, criteriaBuilder) ->
        criteriaBuilder.between(root.get("eventTime"), from, to));
  }

  public static Specification<ReadingEntity> hasSensorIds(List<String> sensorIds) {
    return ((root, query, criteriaBuilder) -> root.get("sensorId").in(sensorIds));
  }

  public static Specification<ReadingEntity> hasGroups(List<String> groups) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.or(
            groups.stream()
                .map(
                    group ->
                        criteriaBuilder.like(
                            root.get("sensorGroup"), String.format("%%%s%%", group)))
                .toArray(Predicate[]::new));
  }
}
