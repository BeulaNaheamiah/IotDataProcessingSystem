package com.iot.commands.domain.port;

import com.iot.commands.domain.model.Reading;

public interface ReadingRepositoryPort {
  void save(Reading reading);
}
