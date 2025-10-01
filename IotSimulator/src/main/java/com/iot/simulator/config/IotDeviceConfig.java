package com.iot.simulator.config;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "iot")
@Validated
@Slf4j
public record IotDeviceConfig(List<IotDeviceProfile> devices) {

  public record IotDeviceProfile(String id, String type, List<String> groups, Behavior behavior) {

    public record Behavior(
        Strategy strategy, Double min, Double max, Double initialValue, Double decrement) {}
  }

  // TODO add validation (e.g. min < max, initialValue between min and max, duplicate device Id,
  // etc.

}
