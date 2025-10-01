package com.iot.simulator.port;

import com.iot.shared.event.ReadingCreatedEvent;

public interface EventProducerPort {

  void send(ReadingCreatedEvent event);
}
