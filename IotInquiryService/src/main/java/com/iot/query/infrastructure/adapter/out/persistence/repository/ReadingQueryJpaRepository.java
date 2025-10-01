package com.iot.query.infrastructure.adapter.out.persistence.repository;

import com.iot.query.infrastructure.adapter.out.persistence.model.ReadingEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadingQueryJpaRepository
    extends JpaRepository<ReadingEntity, UUID>,
        JpaSpecificationExecutor<ReadingEntity>,
        ReadingNativeAggregateRepository,
        ReadingMedianAggregateRepository {}
