package com.iot.query.infrastructure.adapter.in.api.dto;

import com.iot.query.infrastructure.adapter.in.api.validation.ValidMetric;
import com.iot.query.infrastructure.adapter.in.api.validation.ValidTimeStampRange;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.List;

@ValidTimeStampRange
public record ReadingRequestDTO(
    List<String> sensorIds,
    List<String> groups,
    @ValidMetric String metric,
    @NotNull(message = "Start timestamp (from) cannot be null") Instant from,
    @NotNull(message = "Start timestamp (from) cannot be null") Instant to) {

  @AssertTrue(message = "Either sensorIds or groups must be provided.")
  private boolean isSensorIdOrGroupsProvided() {
    boolean isSensorIdPresent = sensorIds != null && !sensorIds.isEmpty();
    boolean isGroupsProvided = groups != null && !groups.isEmpty();
    return isSensorIdPresent || isGroupsProvided;
  }
}
