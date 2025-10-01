package com.iot.query.infrastructure.exception;

public class IotDatabaseDownException extends RuntimeException {
  public IotDatabaseDownException(String message, Throwable cause) {
    super(message, cause);
  }
}
