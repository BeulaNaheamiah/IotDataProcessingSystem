package com.iot.commands.infrastructure.out.persistence;

import static org.mockito.Mockito.*;

import com.iot.commands.domain.model.Reading;
import com.iot.commands.infrastructure.adapter.out.persistence.JpaReadingRepositoryAdapter;
import com.iot.commands.infrastructure.adapter.out.persistence.SpringDataJpaReadingRepository;
import com.iot.commands.infrastructure.adapter.out.persistence.mapper.ReadingMapper;
import com.iot.commands.infrastructure.adapter.out.persistence.model.ReadingEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JpaReadingRepositoryAdapterTest {

  private SpringDataJpaReadingRepository springDataJpaReadingRepository;
  private ReadingMapper mapper;
  private JpaReadingRepositoryAdapter adapter;

  @BeforeEach
  void setUp() {
    springDataJpaReadingRepository = mock(SpringDataJpaReadingRepository.class);
    mapper = mock(ReadingMapper.class);
    adapter = new JpaReadingRepositoryAdapter(springDataJpaReadingRepository, mapper);
  }

  @Test
  void save_shouldMapAndSaveReading() {
    Reading reading = mock(Reading.class);
    ReadingEntity entity = new ReadingEntity();
    when(mapper.toEntity(reading)).thenReturn(entity);

    adapter.save(reading);

    verify(mapper, times(1)).toEntity(reading);
    verify(springDataJpaReadingRepository, times(1)).save(entity);
  }
}
