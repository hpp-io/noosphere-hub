package io.hpp.noosphere.hub.exception;

import io.hpp.noosphere.common.exception.NotFoundException;
import io.hpp.noosphere.hub.config.Constants;

public class UserSubscriptionNotFoundException extends NotFoundException {

  public UserSubscriptionNotFoundException(String value) {
    super(Constants.PROPERTY_NAME_CONTAINER, value);
  }
}
