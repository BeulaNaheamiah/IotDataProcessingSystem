package com.iot.query.infrastructure.adapter.out.persistence.repository;

import com.iot.query.infrastructure.adapter.out.persistence.model.ReadingEntity;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadingNativeAggregateRepository {

  Optional<Double> findAvgAggregateWithSpec(Specification<ReadingEntity> specification);

  Optional<Double> findMaxAggregateWithSpec(Specification<ReadingEntity> specification);

  Optional<Double> findMinAggregateWithSpec(Specification<ReadingEntity> specification);
}
