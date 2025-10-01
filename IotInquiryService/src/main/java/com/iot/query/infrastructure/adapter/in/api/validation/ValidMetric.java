package com.iot.query.infrastructure.adapter.in.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Constraint(validatedBy = MetricValidator.class)
public @interface ValidMetric {
  String message() default "Invalid metric. Allowed values are AVG, MIN, MAX, MEDIAN";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
