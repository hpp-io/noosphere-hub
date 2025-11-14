package io.hpp.noosphere.hub.exception;

import io.hpp.noosphere.hub.config.Constants;

public class VerifierNotFoundException extends NotFoundException {

  public VerifierNotFoundException(String value) {
    super(Constants.PROPERTY_NAME_VERIFIER, value);
  }
}
