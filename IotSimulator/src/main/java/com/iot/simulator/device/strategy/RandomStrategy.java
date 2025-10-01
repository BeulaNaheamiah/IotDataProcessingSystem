package com.iot.simulator.device.strategy;

import java.util.Random;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RandomStrategy implements ReadingGenerationStrategy {

  private final double min;
  private final double max;
  private final Random random = new Random();

  @Override
  public double generate(double currentValue) {
    return random.nextDouble(min, max);
  }
}
