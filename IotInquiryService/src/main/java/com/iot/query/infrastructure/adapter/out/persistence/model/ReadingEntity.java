package com.iot.query.infrastructure.adapter.out.persistence.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "readings_ingestion")
@Data
public class ReadingEntity {
  @Id @GeneratedValue private UUID uuid;
  private String sensorId;
  private String sensorType;
  private String sensorGroup;
  private Instant eventTime;
  private double value;
}
