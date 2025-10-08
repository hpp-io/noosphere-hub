package io.hpp.noosphere.hub.exception;


import io.hpp.noosphere.hub.config.Constants;

public class PermissionDeniedException extends NotFoundException {

  public PermissionDeniedException(String value) {
    super(Constants.PROPERTY_NAME_PERMISSION_DENIED, value);
  }


}
