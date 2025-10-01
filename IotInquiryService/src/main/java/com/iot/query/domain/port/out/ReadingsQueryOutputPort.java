package com.iot.query.domain.port.out;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ReadingsQueryOutputPort {

  Optional<Double> getAverage(List<String> sensorId, List<String> groups, Instant from, Instant to);

  Optional<Double> getMedian(List<String> sensorId, List<String> groups, Instant from, Instant to);

  Optional<Double> getMin(List<String> sensorId, List<String> groups, Instant from, Instant to);

  Optional<Double> getMax(List<String> sensorId, List<String> groups, Instant from, Instant to);
}
