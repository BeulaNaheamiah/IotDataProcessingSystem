package com.iot.simulator.device.strategy;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DecreasingStrategy implements ReadingGenerationStrategy {

  private final double decrement;

  @Override
  public double generate(double currentValue) {
    return currentValue - decrement;
  }
}
