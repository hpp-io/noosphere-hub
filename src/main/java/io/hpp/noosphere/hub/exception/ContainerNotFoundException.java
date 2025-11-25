package io.hpp.noosphere.hub.exception;

import io.hpp.noosphere.common.exception.NotFoundException;
import io.hpp.noosphere.hub.config.Constants;

public class ContainerNotFoundException extends NotFoundException {

  public ContainerNotFoundException(String value) {
    super(Constants.PROPERTY_NAME_CONTAINER, value);
  }
}
