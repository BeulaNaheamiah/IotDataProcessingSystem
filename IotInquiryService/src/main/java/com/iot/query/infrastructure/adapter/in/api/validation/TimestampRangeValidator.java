package com.iot.query.infrastructure.adapter.in.api.validation;

import com.iot.query.infrastructure.adapter.in.api.dto.ReadingRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TimestampRangeValidator
    implements ConstraintValidator<ValidTimeStampRange, ReadingRequestDTO> {
  @Override
  public boolean isValid(
      ReadingRequestDTO request, ConstraintValidatorContext constraintValidatorContext) {
    if (request.from() == null || request.to() == null) {
      return true; // Not null handles it
    }
    // The actual validation logic
    return request.from().isBefore(request.to());
  }
}
