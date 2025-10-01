package com.iot.query.infrastructure.adapter.out.persistence.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ReadingMedianAggregateRepositoryImpl implements ReadingMedianAggregateRepository {

  @PersistenceContext private EntityManager entityManager;

  @Override
  public Optional<Double> findMedianAggregate(
      List<String> sensorIds, List<String> groups, Instant from, Instant to) {

    String baseSql =
        """
                SELECT percentile_cont(0.5) WITHIN GROUP(ORDER BY readings_ingestion.value)
                from readings_ingestion readings_ingestion WHERE readings_ingestion.event_time
                BETWEEN  :from and :to""";

    StringBuilder sql = new StringBuilder(baseSql);
    if (sensorIds != null && !sensorIds.isEmpty()) {
      sql.append(" AND readings_ingestion.sensor_id IN (:sensorIds)");
    } else if (groups != null && !groups.isEmpty()) {
      sql.append(" AND (").append(buildGroupConditions(groups.size())).append(")");
    }

    Query query =
        entityManager
            .createNativeQuery(sql.toString())
            .setParameter("from", from)
            .setParameter("to", to);

    // Bind dynamic parameters
    if (sensorIds != null && !sensorIds.isEmpty()) {
      query.setParameter("sensorIds", sensorIds);
    } else if (groups != null && !groups.isEmpty()) {
      bindGroupParameters(query, groups);
    }

    Double result = (Double) query.getSingleResult();
    return Optional.ofNullable(result);
  }

  private String buildGroupConditions(int groupCount) {
    return IntStream.range(0, groupCount)
        .mapToObj(i -> "r.groups LIKE :group" + i)
        .collect(Collectors.joining(" OR "));
  }

  private void bindGroupParameters(Query query, List<String> groups) {
    IntStream.range(0, groups.size())
        .forEach(i -> query.setParameter("group" + i, "%" + groups.get(i) + "%"));
  }
}
