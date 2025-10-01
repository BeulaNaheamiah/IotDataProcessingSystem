package com.iot.simulator.device;

import com.iot.simulator.config.IotDeviceConfig;
import com.iot.simulator.config.Strategy;
import com.iot.simulator.device.strategy.DecreasingStrategy;
import com.iot.simulator.device.strategy.RandomStrategy;
import com.iot.simulator.device.strategy.ReadingGenerationStrategy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IotDeviceFactory {
  private IotDeviceFactory() {}

  public static IotDevice fromProfile(IotDeviceConfig.IotDeviceProfile profile) {
    if (profile == null || profile.behavior() == null || profile.behavior().strategy() == null) {
      throw new IllegalArgumentException("Device profile/behavior/strategy cannot be null");
    }
    ReadingGenerationStrategy strategy =
        switch (profile.behavior().strategy()) {
          case RANDOM_RANGE -> new RandomStrategy(
              profile.behavior().min(), profile.behavior().max());
          case DECREASING -> new DecreasingStrategy(profile.behavior().decrement());
        };
    double initialValue =
        Strategy.DECREASING.equals(profile.behavior().strategy())
            ? profile.behavior().initialValue()
            : 0.0;

    return new IotDevice(profile, strategy, initialValue);
  }
}
