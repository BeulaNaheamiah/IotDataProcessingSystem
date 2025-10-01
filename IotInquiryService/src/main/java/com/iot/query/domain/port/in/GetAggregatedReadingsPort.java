package com.iot.query.domain.port.in;

import com.iot.query.domain.model.AggregatedReading;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface GetAggregatedReadingsPort {

  Optional<AggregatedReading> getAggregation(
      List<String> sensorIds, List<String> groups, String metric, Instant from, Instant to);
}
