package com.iot.query.application;

import com.iot.query.domain.model.AggregatedReading;
import com.iot.query.domain.port.in.GetAggregatedReadingsPort;
import com.iot.query.domain.port.out.ReadingsQueryOutputPort;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public record ReadingsQueryService(ReadingsQueryOutputPort outputPort)
    implements GetAggregatedReadingsPort {

  @Override
  public Optional<AggregatedReading> getAggregation(
      List<String> sensorIds, List<String> groups, String metric, Instant from, Instant to) {
    return switch (metric.toUpperCase()) {
      case "AVG" -> outputPort
          .getAverage(sensorIds, groups, from, to)
          .map(value -> new AggregatedReading(metric, value));
      case "MEDIAN" -> outputPort
          .getMedian(sensorIds, groups, from, to)
          .map(value -> new AggregatedReading(metric, value));
      case "MIN" -> outputPort
          .getMin(sensorIds, groups, from, to)
          .map(value -> new AggregatedReading(metric, value));
      case "MAX" -> outputPort
          .getMax(sensorIds, groups, from, to)
          .map(value -> new AggregatedReading(metric, value));
      default -> Optional.empty();
    };
  }
}
