package com.iot.query.infrastructure.adapter.in.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;

public class MetricValidator implements ConstraintValidator<ValidMetric, String> {

  private static final Set<String> ALLOWED_METRICS = Set.of("AVG", "MIN", "MAX", "MEDIAN");

  @Override
  public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
    if (value == null || value.isBlank()) {
      return false;
    }
    return ALLOWED_METRICS.contains(value.toUpperCase());
  }
}
