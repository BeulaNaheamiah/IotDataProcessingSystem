package com.iot.query.infrastructure.adapter.in.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.query.domain.model.AggregatedReading;
import com.iot.query.domain.port.in.GetAggregatedReadingsPort;
import com.iot.query.infrastructure.adapter.in.api.dto.ReadingRequestDTO;
import com.iot.query.infrastructure.security.CustomAuthenticationEntryPoint;
import com.iot.query.infrastructure.security.SecurityConfig;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ReadingController.class)
@Import({SecurityConfig.class, CustomAuthenticationEntryPoint.class})
@TestPropertySource(locations = "classpath:application-test.properties")
class ReadingControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private GetAggregatedReadingsPort aggregatedReadingsPort;

  @Test
  @WithMockUser(roles = "OPERATOR")
  void whenValidRequest_shouldReturn200Ok() throws Exception {
    ReadingRequestDTO request =
        new ReadingRequestDTO(
            List.of("sensor-1"), null, "AVG", Instant.now().minusSeconds(3600), Instant.now());
    AggregatedReading result = new AggregatedReading("AVG", 55.5);
    when(aggregatedReadingsPort.getAggregation(any(), any(), any(), any(), any()))
        .thenReturn(Optional.of(result));

    mockMvc
        .perform(
            post("/api/readings/v1/aggregate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.metric").value("AVG"))
        .andExpect(jsonPath("$.value").value(55.5));
  }

  @Test
  @DisplayName("Bad Request when sensorIds and groups are both null")
  @WithMockUser(roles = "OPERATOR")
  void whenInvalidRequest_shouldReturn400BadRequest() throws Exception {
    ReadingRequestDTO request =
        new ReadingRequestDTO(null, null, "AVG", Instant.now().minusSeconds(3600), Instant.now());

    mockMvc
        .perform(
            post("/api/readings/v1/aggregate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void whenNotAuthenticated_shouldReturn401Unauthorized() throws Exception {
    ReadingRequestDTO request =
        new ReadingRequestDTO(
            List.of("sensor-1"), null, "AVG", Instant.now().minusSeconds(3600), Instant.now());

    mockMvc
        .perform(
            post("/api/readings/aggregate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }
}
