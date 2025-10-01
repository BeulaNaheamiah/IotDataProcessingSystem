package com.iot.simulator.service;

import com.iot.shared.event.ReadingCreatedEvent;
import com.iot.simulator.config.IotDeviceConfig;
import com.iot.simulator.device.IotDevice;
import com.iot.simulator.device.IotDeviceFactory;
import com.iot.simulator.port.EventProducerPort;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SimulatorService {

  private final EventProducerPort eventProducerPort;

  private List<IotDevice> iotDevices = new ArrayList<>();

  public SimulatorService(EventProducerPort eventProducerPort, IotDeviceConfig iotDeviceConfig) {
    this.eventProducerPort = eventProducerPort;
    log.info("Simulator service created {}", iotDeviceConfig.devices());

    createIotDevices(iotDeviceConfig);
  }

  private void createIotDevices(IotDeviceConfig iotDeviceConfig) {
    if (iotDeviceConfig.devices() != null && !iotDeviceConfig.devices().isEmpty()) {
      this.iotDevices =
          iotDeviceConfig.devices().stream().map(IotDeviceFactory::fromProfile).toList();
    } else {
      log.warn("No IoT devices configured!");
    }
  }

  @Scheduled(fixedRate = 1000)
  public void generateAndSendReadings() {

    if (iotDevices.isEmpty()) {
      log.warn("Simulator device list is empty!");
      return;
    }
    iotDevices.parallelStream()
        .forEach(
            iotDevice -> {
              var event =
                  new ReadingCreatedEvent(
                      iotDevice.getId(),
                      iotDevice.getType(),
                      iotDevice.getGroups(),
                      Instant.now(),
                      iotDevice.generatedReadingValue());
              eventProducerPort.send(event);
            });
  }
}
