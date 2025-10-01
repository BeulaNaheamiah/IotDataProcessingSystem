package com.iot.commands.infrastructure.adapter.out.persistence;

import com.iot.commands.infrastructure.adapter.out.persistence.model.ReadingEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataJpaReadingRepository extends JpaRepository<ReadingEntity, UUID> {}
