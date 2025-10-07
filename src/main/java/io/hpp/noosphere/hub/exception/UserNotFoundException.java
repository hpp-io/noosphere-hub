package io.hpp.noosphere.hub.exception;


import io.hpp.noosphere.hub.config.Constants;

public class UserNotFoundException extends NotFoundException {

  public UserNotFoundException(String value) {
    super(Constants.PROPERTY_NAME_USER, value);
  }

}
