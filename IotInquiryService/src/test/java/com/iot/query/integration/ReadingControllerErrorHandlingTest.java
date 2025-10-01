package com.iot.query.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.query.application.ReadingsQueryService;
import com.iot.query.infrastructure.adapter.in.api.dto.ReadingRequestDTO;
import com.iot.query.infrastructure.adapter.out.persistence.ReadingsQueryOutputAdapter;
import com.iot.query.infrastructure.adapter.out.persistence.repository.ReadingQueryJpaRepository;
import com.iot.query.infrastructure.security.CustomAuthenticationEntryPoint;
import com.iot.query.infrastructure.security.SecurityConfig;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@Import({
  SecurityConfig.class,
  CustomAuthenticationEntryPoint.class,
  ReadingsQueryService.class,
  ReadingsQueryOutputAdapter.class
})
class ReadingControllerErrorHandlingTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  // We mock the repository layer for this test
  @MockitoBean private ReadingQueryJpaRepository repository;

  @Test
  @WithMockUser(roles = "OPERATOR")
  void whenDatabaseIsDown_shouldReturn503ServiceUnavailable() throws Exception {
    // Arrange
    ReadingRequestDTO request =
        new ReadingRequestDTO(
            List.of("sensor-1"),
            null,
            "AVG",
            Instant.now().minus(2, ChronoUnit.HOURS),
            Instant.now().plus(1, ChronoUnit.HOURS));

    when(repository.findAvgAggregateWithSpec(any()))
        .thenThrow(new DataAccessException("Unable to connect to database") {});

    mockMvc
        .perform(
            post("/api/readings/v1/aggregate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isServiceUnavailable())
        .andExpect(jsonPath("$.error").value("Service Unavailable"))
        .andExpect(
            jsonPath("$.message")
                .value(
                    "The service is temporarily unable to connect to the database. Please try again later."));
  }
}
