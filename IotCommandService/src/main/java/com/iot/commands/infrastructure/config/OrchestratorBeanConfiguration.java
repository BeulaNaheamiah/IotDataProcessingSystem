package com.iot.commands.infrastructure.config;

import com.iot.commands.application.service.ReadingOrchestratorService;
import com.iot.commands.domain.port.ReadingRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrchestratorBeanConfiguration {

  @Bean
  public ReadingOrchestratorService readingOrchestratorService(
      ReadingRepositoryPort readingRepositoryPort) {
    return new ReadingOrchestratorService(readingRepositoryPort);
  }
}
