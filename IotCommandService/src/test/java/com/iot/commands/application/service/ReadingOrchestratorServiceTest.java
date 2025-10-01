package com.iot.commands.application.service;

import static org.mockito.Mockito.*;

import com.iot.commands.domain.model.Reading;
import com.iot.commands.domain.port.ReadingRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ReadingOrchestratorServiceTest {

  @Mock private ReadingRepositoryPort readingRepositoryPort;

  private ReadingOrchestratorService readingOrchestratorService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    readingOrchestratorService = new ReadingOrchestratorService(readingRepositoryPort);
  }

  @Test
  void process_shouldSaveReading_whenReadingIsNotNull() {
    Reading reading = mock(Reading.class);

    readingOrchestratorService.process(reading);

    verify(readingRepositoryPort, times(1)).save(reading);
  }

  @Test
  void process_shouldLogWarningAndSave_whenReadingIsNull() {
    readingOrchestratorService.process(null);

    verify(readingRepositoryPort, times(1)).save(null);
  }
}
