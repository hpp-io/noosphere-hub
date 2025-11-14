package io.hpp.noosphere.hub.exception;

import io.hpp.noosphere.hub.config.Constants;

public class AgentRequestNotFoundException extends NotFoundException {

  public AgentRequestNotFoundException(String value) {
    super(Constants.PROPERTY_NAME_AGENT_REQUEST, value);
  }
}
