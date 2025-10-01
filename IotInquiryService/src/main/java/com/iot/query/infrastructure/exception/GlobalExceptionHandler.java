package com.iot.query.infrastructure.exception;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

    // Handle the custom class level validation
    ex.getBindingResult()
        .getGlobalErrors()
        .forEach(error -> errors.put(error.getObjectName(), error.getDefaultMessage()));

    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(IotDatabaseDownException.class)
  public ResponseEntity<Map<String, String>> handleIotDatabaseDownException(
      IotDatabaseDownException ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", "Service Unavailable");
    error.put(
        "message",
        "The service is temporarily unable to connect to the database. Please try again later.");

    return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
  }

  // TODO: handle other exceptions as needed
}
