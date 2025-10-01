package com.iot.simulator.adapter;

import com.iot.shared.event.ReadingCreatedEvent;
import com.iot.simulator.port.EventProducerPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventProducerAdapter implements EventProducerPort {

  private final KafkaTemplate<String, ReadingCreatedEvent> kafkaTemplate;

  @Value("${app.iot.kafka.topic:iot-reading-topic}")
  private String topicName;

  @Override
  @Retryable(
      retryFor = {KafkaException.class},
      maxAttempts = 5,
      backoff = @Backoff(delay = 2000, multiplier = 2)) // Exponential backoff: 2s, 4s, 8s,
  public void send(ReadingCreatedEvent event) {
    kafkaTemplate.send(topicName, event.sensorId(), event);
  }

  @Recover
  public void recover(KafkaException kafkaException, ReadingCreatedEvent readingCreatedEvent) {
    log.error(
        "All retry attempts to send event failed. Event: {}, Error: {}",
        readingCreatedEvent,
        kafkaException.getMessage());
  }
}
