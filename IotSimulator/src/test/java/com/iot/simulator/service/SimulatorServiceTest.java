package com.iot.simulator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import com.iot.shared.event.ReadingCreatedEvent;
import com.iot.simulator.config.IotDeviceConfig;
import com.iot.simulator.config.Strategy;
import com.iot.simulator.port.EventProducerPort;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class SimulatorServiceTest {

  private EventProducerPort eventProducerPort;
  private SimulatorService simulatorService;

  @BeforeEach
  void setUp() {
    eventProducerPort = mock(EventProducerPort.class);

    // Sample device config
    IotDeviceConfig iotDeviceConfig = mock(IotDeviceConfig.class);
    when(iotDeviceConfig.devices())
        .thenReturn(
            List.of(
                getIotDeviceProfile("device1", Strategy.RANDOM_RANGE),
                getIotDeviceProfile("device2", Strategy.RANDOM_RANGE)));

    simulatorService = new SimulatorService(eventProducerPort, iotDeviceConfig);
  }

  private static IotDeviceConfig.IotDeviceProfile getIotDeviceProfile(
      String deviceId, Strategy strategy) {
    return new IotDeviceConfig.IotDeviceProfile(
        deviceId,
        "temperature",
        List.of("group1"),
        new IotDeviceConfig.IotDeviceProfile.Behavior(strategy, 0.0, 100.0, 0.0, 0.0));
  }

  @Test
  void testGenerateAndSendReadings_sendsEventsForAllDevices() {
    simulatorService.generateAndSendReadings();

    ArgumentCaptor<ReadingCreatedEvent> captor = ArgumentCaptor.forClass(ReadingCreatedEvent.class);
    verify(eventProducerPort, times(2)).send(captor.capture());

    List<ReadingCreatedEvent> events = captor.getAllValues();

    assertEquals(2, events.size());
    assertTrue(events.stream().anyMatch(e -> e.sensorId().equals("device1")));
    assertTrue(events.stream().anyMatch(e -> e.sensorId().equals("device2")));
  }

  @Test
  void testGenerateAndSendReadings_withEmptyDeviceList_logsWarning() {
    IotDeviceConfig emptyConfig = mock(IotDeviceConfig.class);
    when(emptyConfig.devices()).thenReturn(List.of());

    SimulatorService emptySimulator = new SimulatorService(eventProducerPort, emptyConfig);
    emptySimulator.generateAndSendReadings();

    verify(eventProducerPort, never()).send(any());
  }

  @Test
  void testGenerateAndSendReadings_withDecreasingBehavior_configured() {
    IotDeviceConfig deviceConfigWithUndefinedStrategy = mock(IotDeviceConfig.class);
    when(deviceConfigWithUndefinedStrategy.devices())
        .thenReturn(List.of(getIotDeviceProfile("device1", Strategy.DECREASING)));

    SimulatorService emptySimulator =
        new SimulatorService(eventProducerPort, deviceConfigWithUndefinedStrategy);
    emptySimulator.generateAndSendReadings();

    ArgumentCaptor<ReadingCreatedEvent> captor = ArgumentCaptor.forClass(ReadingCreatedEvent.class);
    verify(eventProducerPort, times(1)).send(captor.capture());

    List<ReadingCreatedEvent> events = captor.getAllValues();
    assertEquals(1, events.size());
    assertTrue(events.stream().anyMatch(e -> e.sensorId().equals("device1")));
    assertTrue(events.stream().anyMatch(e -> e.readings() == 0.0));
  }
}
