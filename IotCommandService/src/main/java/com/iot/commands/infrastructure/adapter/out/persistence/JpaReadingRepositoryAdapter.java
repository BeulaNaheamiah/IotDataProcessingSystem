package com.iot.commands.infrastructure.adapter.out.persistence;

import com.iot.commands.domain.model.Reading;
import com.iot.commands.domain.port.ReadingRepositoryPort;
import com.iot.commands.infrastructure.adapter.out.persistence.mapper.ReadingMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JpaReadingRepositoryAdapter implements ReadingRepositoryPort {

  private final SpringDataJpaReadingRepository springDataJpaReadingRepository;
  private final ReadingMapper mapper;

  @Retryable(
      retryFor = {DataAccessException.class},
      maxAttempts = 5,
      backoff = @Backoff(delay = 2000, multiplier = 2)) // Exponential backoff: 2s, 4s, 8s, etc.
  @Override
  public void save(Reading reading) {
    springDataJpaReadingRepository.save(mapper.toEntity(reading));
  }

  @Recover
  public void recover(DataAccessException dataAccessException, Reading reading) {
    log.error(
        "Failed to save reading for sensor {} after multiple retries. Error: {}",
        reading.sensorId(),
        dataAccessException
            .getMessage()); // TODO: This is critical failure, consider alerting mechanisms
  }
}
