package com.iot.query.infrastructure.config;

import com.iot.query.application.ReadingsQueryService;
import com.iot.query.domain.port.out.ReadingsQueryOutputPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueryBeanConfiguration {

  @Bean
  public ReadingsQueryService readingsQueryService(
      ReadingsQueryOutputPort readingsQueryOutputPort) {
    return new ReadingsQueryService(readingsQueryOutputPort);
  }
}
