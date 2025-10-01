package com.iot.query.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.query.infrastructure.adapter.in.api.dto.ReadingRequestDTO;
import com.iot.query.infrastructure.adapter.out.persistence.model.ReadingEntity;
import com.iot.query.infrastructure.adapter.out.persistence.repository.ReadingQueryJpaRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class ReadingControllerIntegrationTest {

  public static final String PATH = "/api/readings/v1/aggregate";

  @Container
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>(
          DockerImageName.parse("timescale/timescaledb:latest-pg16")
              .asCompatibleSubstituteFor("postgres"));

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private ReadingQueryJpaRepository repository;

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @BeforeEach
  void setUp() {
    // Clean up and seed the database before each test
    repository.deleteAll();

    Instant now = Instant.now();
    ReadingEntity reading1 = new ReadingEntity();
    reading1.setSensorId("sensor-1");
    reading1.setValue(10.0);
    reading1.setEventTime(now.minus(1, ChronoUnit.HOURS));
    reading1.setSensorGroup("env,zone-a");

    ReadingEntity reading2 = new ReadingEntity();
    reading2.setSensorId("sensor-1");
    reading2.setValue(20.0);
    reading2.setEventTime(now);
    reading1.setSensorGroup("env1,zone-a");

    repository.saveAll(List.of(reading1, reading2));
  }

  @Test
  @WithMockUser(roles = "OPERATOR")
  void whenValidRequest_shouldReturnCorrectAverage() throws Exception {

    ReadingRequestDTO request =
        new ReadingRequestDTO(
            List.of("sensor-1"),
            null,
            "AVG",
            Instant.now().minus(2, ChronoUnit.HOURS),
            Instant.now().plus(1, ChronoUnit.HOURS));

    mockMvc
        .perform(
            post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.metric").value("AVG"))
        .andExpect(jsonPath("$.value").value(15.0));
  }

  @Test
  @WithMockUser(roles = "OPERATOR")
  void whenValidRequest_shouldReturnCorrectMin() throws Exception {

    ReadingRequestDTO request =
        new ReadingRequestDTO(
            List.of("sensor-1"),
            null,
            "MIN",
            Instant.now().minus(2, ChronoUnit.HOURS),
            Instant.now().plus(1, ChronoUnit.HOURS));

    mockMvc
        .perform(
            post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.metric").value("MIN"))
        .andExpect(jsonPath("$.value").value(10.0));
  }

  @Test
  @WithMockUser(roles = "OPERATOR")
  void whenValidRequest_shouldReturnCorrectMax() throws Exception {

    ReadingRequestDTO request =
        new ReadingRequestDTO(
            List.of("sensor-1"),
            null,
            "MAX",
            Instant.now().minus(2, ChronoUnit.HOURS),
            Instant.now().plus(1, ChronoUnit.HOURS));

    mockMvc
        .perform(
            post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.metric").value("MAX"))
        .andExpect(jsonPath("$.value").value(20.0));
  }

  @Test
  @WithMockUser(roles = "OPERATOR")
  void whenValidRequest_shouldReturnCorrectMedian() throws Exception {

    ReadingRequestDTO request =
        new ReadingRequestDTO(
            List.of("sensor-1"),
            null,
            "MEDIAN",
            Instant.now().minus(2, ChronoUnit.HOURS),
            Instant.now().plus(1, ChronoUnit.HOURS));

    mockMvc
        .perform(
            post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.metric").value("MEDIAN"))
        .andExpect(jsonPath("$.value").value(15.0));
  }

  @Test
  @WithMockUser(roles = "OPERATOR")
  void whenValidRequest_shouldReturnCorrectAverageWithGroups() throws Exception {

    ReadingRequestDTO request =
        new ReadingRequestDTO(
            List.of("sensor-1"),
            List.of("zone-a"),
            "AVG",
            Instant.now().minus(2, ChronoUnit.HOURS),
            Instant.now().plus(1, ChronoUnit.HOURS));

    mockMvc
        .perform(
            post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.metric").value("AVG"))
        .andExpect(jsonPath("$.value").value(15.0));
  }

  @Test
  @WithMockUser(roles = "OPERATOR")
  void whenNoDataFound_shouldReturn404NotFound() throws Exception {
    ReadingRequestDTO request =
        new ReadingRequestDTO(
            List.of("non-existent-sensor"), // A sensor with no data
            null,
            "AVG",
            Instant.now().minus(2, ChronoUnit.HOURS),
            Instant.now().plus(1, ChronoUnit.HOURS));

    mockMvc
        .perform(
            post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  void whenNotAuthenticated_shouldReturn401Unauthorized() throws Exception {
    ReadingRequestDTO request =
        new ReadingRequestDTO(List.of("sensor-1"), null, "AVG", Instant.now(), Instant.now());

    mockMvc
        .perform(
            post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(roles = "OPERATOR")
  void whenInvalidMetric_shouldReturn400BadRequest() throws Exception {
    ReadingRequestDTO request =
        new ReadingRequestDTO(
            List.of("sensor-1"),
            null,
            "INVALID_METRIC", // Invalid value
            Instant.now().minus(2, ChronoUnit.HOURS),
            Instant.now().plus(1, ChronoUnit.HOURS));

    mockMvc
        .perform(
            post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.metric").value("Invalid metric. Allowed values are AVG, MIN, MAX, MEDIAN"));
  }
}
