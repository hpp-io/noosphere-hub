package io.hpp.noosphere.hub.exception;


import io.hpp.noosphere.hub.config.Constants;

public class TooManyUsersException extends InvalidDataException {

  public TooManyUsersException(String value) {
    super(Constants.PROPERTY_NAME_USER, value);
  }

}
