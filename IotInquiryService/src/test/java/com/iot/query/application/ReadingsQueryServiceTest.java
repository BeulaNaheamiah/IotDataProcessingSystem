package com.iot.query.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.iot.query.domain.model.AggregatedReading;
import com.iot.query.domain.port.out.ReadingsQueryOutputPort;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReadingsQueryServiceTest {

  @Mock private ReadingsQueryOutputPort readingQueryPort;

  private ReadingsQueryService readingQueryService;

  @BeforeEach
  void setUp() {
    readingQueryService = new ReadingsQueryService(readingQueryPort);
  }

  @Test
  void whenCalculatingAverage_shouldReturnCorrectValue() {

    when(readingQueryPort.getAverage(any(), any(), any(), any())).thenReturn(Optional.of(15.0));

    Optional<AggregatedReading> result =
        readingQueryService.getAggregation(List.of("s1"), null, "AVG", Instant.MIN, Instant.MAX);

    assertThat(result).isPresent();
    assertThat(result.get().metric()).isEqualTo("AVG");
    assertThat(result.get().value()).isEqualTo(15.0);
  }

  @Test
  void whenCalculatingMin_shouldReturnCorrectValue() {

    when(readingQueryPort.getMin(any(), any(), any(), any())).thenReturn(Optional.of(0.0));

    Optional<AggregatedReading> result =
        readingQueryService.getAggregation(List.of("s1"), null, "MIN", Instant.MIN, Instant.MAX);

    assertThat(result).isPresent();
    assertThat(result.get().metric()).isEqualTo("MIN");
    assertThat(result.get().value()).isEqualTo(0.0);
  }

  @Test
  void whenCalculatingMax_shouldReturnCorrectValue() {

    when(readingQueryPort.getMax(any(), any(), any(), any())).thenReturn(Optional.of(15.0));

    Optional<AggregatedReading> result =
        readingQueryService.getAggregation(List.of("s1"), null, "MAX", Instant.MIN, Instant.MAX);

    assertThat(result).isPresent();
    assertThat(result.get().metric()).isEqualTo("MAX");
    assertThat(result.get().value()).isEqualTo(15.0);
  }

  @Test
  void whenCalculatingMedian_shouldReturnCorrectValue() {

    when(readingQueryPort.getMedian(any(), any(), any(), any())).thenReturn(Optional.of(40.0));

    Optional<AggregatedReading> result =
        readingQueryService.getAggregation(List.of("s1"), null, "MEDIAN", Instant.MIN, Instant.MAX);

    assertThat(result).isPresent();
    assertThat(result.get().metric()).isEqualTo("MEDIAN");
    assertThat(result.get().value()).isEqualTo(40.0);
  }

  @Test
  void whenNoReadingsFound_shouldReturnEmpty() {
    // Arrange
    when(readingQueryPort.getAverage(any(), any(), any(), any())).thenReturn(Optional.empty());

    // Act
    Optional<AggregatedReading> result =
        readingQueryService.getAggregation(List.of("s1"), null, "AVG", Instant.MIN, Instant.MAX);

    // Assert
    assertThat(result).isEmpty();
  }
}
