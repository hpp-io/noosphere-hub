package io.hpp.noosphere.hub.exception;


import io.hpp.noosphere.hub.config.Constants;

public class GroupNotFoundException extends NotFoundException {

  public GroupNotFoundException(String value) {
    super(Constants.PROPERTY_NAME_GROUP, value);
  }

}
