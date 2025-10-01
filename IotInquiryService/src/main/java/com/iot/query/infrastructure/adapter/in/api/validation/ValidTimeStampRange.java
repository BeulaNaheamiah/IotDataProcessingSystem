package com.iot.query.infrastructure.adapter.in.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = TimestampRangeValidator.class)
public @interface ValidTimeStampRange {
  String message() default "'from' timestamp must be before 'to' timestamp";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
