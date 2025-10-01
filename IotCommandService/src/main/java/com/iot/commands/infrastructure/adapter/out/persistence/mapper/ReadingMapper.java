package com.iot.commands.infrastructure.adapter.out.persistence.mapper;

import com.iot.commands.domain.model.Reading;
import com.iot.commands.infrastructure.adapter.out.persistence.model.ReadingEntity;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ReadingMapper {
  private static String getSensorGroup(List<String> groups) {
    return groups != null && !groups.isEmpty() ? String.join(",", groups) : "";
  }

  private static List<String> getListGroups(String listOfGroup) {
    return listOfGroup != null && !listOfGroup.isEmpty()
        ? Arrays.stream(listOfGroup.split(",")).toList()
        : List.of();
  }

  public ReadingEntity toEntity(Reading reading) {

    var entity = new ReadingEntity();
    entity.setSensorId(reading.sensorId());
    entity.setSensorType(reading.sensorType());
    entity.setSensorGroup(getSensorGroup(reading.groups()));
    entity.setEventTime(reading.eventTime());
    entity.setValue(reading.value());
    return entity;
  }

  public Reading toDomain(ReadingEntity entity) {
    return new Reading(
        entity.getSensorId(),
        entity.getSensorType(),
        getListGroups(entity.getSensorGroup()),
        entity.getEventTime(),
        entity.getValue());
  }
}
