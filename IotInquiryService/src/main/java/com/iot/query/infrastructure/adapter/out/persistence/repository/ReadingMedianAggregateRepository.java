package com.iot.query.infrastructure.adapter.out.persistence.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadingMedianAggregateRepository {

  Optional<Double> findMedianAggregate(
      List<String> sensorIds, List<String> groups, Instant from, Instant to);
}
