package com.iot.commands.application.service;

import com.iot.commands.domain.model.Reading;
import com.iot.commands.domain.port.ReadingIngestionPort;
import com.iot.commands.domain.port.ReadingRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ReadingOrchestratorService implements ReadingIngestionPort {

  private final ReadingRepositoryPort readingRepositoryPort;

  @Override
  public void process(Reading reading) {
    if (reading == null) {
      log.warn("reading event is null");
    }
    log.debug("Received Reading Event to process {}", reading);
    // TODO: business logic or validation & enrichment
    readingRepositoryPort.save(reading);
  }
}
