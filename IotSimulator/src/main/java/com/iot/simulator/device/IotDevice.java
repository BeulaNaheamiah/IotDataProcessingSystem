package com.iot.simulator.device;

import com.iot.simulator.config.IotDeviceConfig;
import com.iot.simulator.device.strategy.ReadingGenerationStrategy;
import java.util.List;

public class IotDevice {
  private final IotDeviceConfig.IotDeviceProfile profile;
  private final ReadingGenerationStrategy readingGenerationStrategy;
  private double currentValue;

  public IotDevice(
      IotDeviceConfig.IotDeviceProfile profile,
      ReadingGenerationStrategy readingGenerationStrategy,
      double initialValue) {
    this.profile = profile;
    this.readingGenerationStrategy = readingGenerationStrategy;
    this.currentValue = initialValue;
  }

  public String getId() {
    return profile.id();
  }

  public String getType() {
    return profile.type();
  }

  public List<String> getGroups() {
    return profile.groups();
  }

  public double generatedReadingValue() {
    if (profile.behavior() == null || profile.behavior().strategy() == null) {
      return 0.0;
    }
    currentValue = readingGenerationStrategy.generate(currentValue);
    return currentValue;
  }
}
