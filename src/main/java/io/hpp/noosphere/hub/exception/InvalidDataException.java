package io.hpp.noosphere.hub.exception;


import io.hpp.noosphere.hub.config.Constants;

public class InvalidDataException extends PropertyValueAlertException {

  public InvalidDataException(String propertyName, String value) {
    super(ErrorConstants.CONSTRAINT_VIOLATION_TYPE, Constants.ERROR_KEY_INVALID, propertyName, value);
  }
  public InvalidDataException(String message, String propertyName, String value) {
    super(ErrorConstants.CONSTRAINT_VIOLATION_TYPE, message, Constants.ERROR_KEY_INVALID, propertyName, value);
  }
  public InvalidDataException(String propertyName) {
    super(ErrorConstants.CONSTRAINT_VIOLATION_TYPE, Constants.ERROR_KEY_INVALID, propertyName, null);
  }


}
