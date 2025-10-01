package com.iot.commands.domain.model;

import java.time.Instant;
import java.util.List;

public record Reading(
    String sensorId, String sensorType, List<String> groups, Instant eventTime, double value) {}
