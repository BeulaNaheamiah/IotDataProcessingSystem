package com.iot.shared.event;

import java.time.Instant;
import java.util.List;

public record ReadingCreatedEvent(
    String sensorId, String sensorType, List<String> groups, Instant eventTime, double readings) {}
