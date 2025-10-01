package com.iot.simulator;

import com.iot.simulator.config.IotDeviceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(IotDeviceConfig.class)
@EnableRetry
public class IotSimulatorApplication {

  public static void main(String[] args) {
    SpringApplication.run(IotSimulatorApplication.class, args);
  }
}
