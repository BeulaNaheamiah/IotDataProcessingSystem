package com.iot.query.infrastructure.adapter.out.persistence;

import com.iot.query.domain.port.out.ReadingsQueryOutputPort;
import com.iot.query.infrastructure.adapter.out.persistence.model.ReadingEntity;
import com.iot.query.infrastructure.adapter.out.persistence.repository.ReadingQueryJpaRepository;
import com.iot.query.infrastructure.exception.IotDatabaseDownException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReadingsQueryOutputAdapter implements ReadingsQueryOutputPort {

  public static final String DATABASE_IS_CURRENTLY_UNAVAILABLE =
      "Database is currently unavailable";
  private final ReadingQueryJpaRepository readingQueryJpaRepository;

  private Specification<ReadingEntity> buildSpecification(
      List<String> sensorIds, List<String> groups, Instant from, Instant to) {
    Specification<ReadingEntity> specification =
        ReadingQuerySpecification.isBetweenTimeStamp(from, to);
    if (sensorIds != null && !sensorIds.isEmpty()) {
      specification = specification.and(ReadingQuerySpecification.hasSensorIds(sensorIds));
    } else if (groups != null && !groups.isEmpty()) {
      specification = specification.and(ReadingQuerySpecification.hasGroups(groups));
    }
    return specification;
  }

  @Override
  public Optional<Double> getAverage(
      List<String> sensorId, List<String> groups, Instant from, Instant to) {
    try {
      Specification<ReadingEntity> specification = buildSpecification(sensorId, groups, from, to);
      return readingQueryJpaRepository.findAvgAggregateWithSpec(specification);
    } catch (DataAccessException e) {
      throw new IotDatabaseDownException(DATABASE_IS_CURRENTLY_UNAVAILABLE, e);
    }
  }

  @Override
  public Optional<Double> getMin(
      List<String> sensorId, List<String> groups, Instant from, Instant to) {
    try {
      Specification<ReadingEntity> specification = buildSpecification(sensorId, groups, from, to);
      return readingQueryJpaRepository.findMinAggregateWithSpec(specification);
    } catch (DataAccessException e) {
      throw new IotDatabaseDownException(DATABASE_IS_CURRENTLY_UNAVAILABLE, e);
    }
  }

  @Override
  public Optional<Double> getMax(
      List<String> sensorId, List<String> groups, Instant from, Instant to) {
    try {
      Specification<ReadingEntity> specification = buildSpecification(sensorId, groups, from, to);
      return readingQueryJpaRepository.findMaxAggregateWithSpec(specification);
    } catch (DataAccessException e) {
      throw new IotDatabaseDownException(DATABASE_IS_CURRENTLY_UNAVAILABLE, e);
    }
  }

  @Override
  public Optional<Double> getMedian(
      List<String> sensorId, List<String> groups, Instant from, Instant to) {
    try {
      return readingQueryJpaRepository.findMedianAggregate(sensorId, groups, from, to);
    } catch (DataAccessException e) {
      throw new IotDatabaseDownException(DATABASE_IS_CURRENTLY_UNAVAILABLE, e);
    }
  }
}
