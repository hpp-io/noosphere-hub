package io.hpp.noosphere.hub.exception;


import io.hpp.noosphere.hub.config.Constants;

public class AlreadyExistsException extends PropertyValueAlertException {

  public AlreadyExistsException(String propertyName, String value) {
    super(ErrorConstants.CONSTRAINT_VIOLATION_TYPE, Constants.ERROR_KEY_ALREADY_EXISTS, propertyName, value);
  }

  public AlreadyExistsException(String message, String propertyName, String value) {
    super(ErrorConstants.CONSTRAINT_VIOLATION_TYPE, message, Constants.ERROR_KEY_ALREADY_EXISTS, propertyName, value);
  }


}
