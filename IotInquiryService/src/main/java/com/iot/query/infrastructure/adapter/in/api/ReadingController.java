package com.iot.query.infrastructure.adapter.in.api;

import com.iot.query.domain.model.AggregatedReading;
import com.iot.query.domain.port.in.GetAggregatedReadingsPort;
import com.iot.query.infrastructure.adapter.in.api.dto.ReadingRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/readings/v1")
@RequiredArgsConstructor
public class ReadingController {

  private final GetAggregatedReadingsPort aggregatedReadingsPort;

  @PostMapping("/aggregate")
  public ResponseEntity<AggregatedReading> getAggregatedReading(
      @Valid @RequestBody ReadingRequestDTO readingRequestDTO) {

    log.info("ReadingRequestDTO:{}", readingRequestDTO);

    return aggregatedReadingsPort
        .getAggregation(
            readingRequestDTO.sensorIds(),
            readingRequestDTO.groups(),
            readingRequestDTO.metric(),
            readingRequestDTO.from(),
            readingRequestDTO.to())
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
}
