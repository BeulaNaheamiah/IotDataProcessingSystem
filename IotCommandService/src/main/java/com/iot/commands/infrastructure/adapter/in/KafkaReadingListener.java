package com.iot.commands.infrastructure.adapter.in;

import com.iot.commands.domain.model.Reading;
import com.iot.commands.domain.port.ReadingIngestionPort;
import com.iot.shared.event.ReadingCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaReadingListener {

  private final ReadingIngestionPort readingIngestionPort;

  @Retryable(
      retryFor = {KafkaException.class},
      maxAttempts = 5,
      backoff = @Backoff(delay = 2000, multiplier = 2)) // Exponential backoff: 2s, 4s, 8s, etc.
  @KafkaListener(topics = "${app.iot.kafka.topic}", groupId = "${app.iot.kafka.consumer-group-id}")
  public void handleReadingEvent(ReadingCreatedEvent readingCreatedEvent) {

    log.info("Received ReadingCreatedEvent from topic {}", readingCreatedEvent);
    var reading =
        new Reading(
            readingCreatedEvent.sensorId(),
            readingCreatedEvent.sensorType(),
            readingCreatedEvent.groups(),
            readingCreatedEvent.eventTime(),
            readingCreatedEvent.readings());
    readingIngestionPort.process(reading);
  }

  @Recover
  public void recover(KafkaException kafkaException, ReadingCreatedEvent readingCreatedEvent) {
    log.error(
        "All retry attempts to process kafka event failed. Event: {}, Error: {}",
        readingCreatedEvent,
        kafkaException.getMessage()); // TODO: Critical failure, consider alerting mechanisms
  }
}
