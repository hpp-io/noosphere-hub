package io.hpp.noosphere.hub.exception;


import io.hpp.noosphere.hub.config.Constants;

public class ValidatorNotFoundException extends NotFoundException {

  public ValidatorNotFoundException(String value) {
    super(Constants.PROPERTY_NAME_VALIDATOR, value);
  }

}
