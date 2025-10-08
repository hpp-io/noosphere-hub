package io.hpp.noosphere.hub.exception;


import io.hpp.noosphere.hub.config.Constants;

public class AgentNotFoundException extends NotFoundException {

  public AgentNotFoundException(String value) {
    super(Constants.PROPERTY_NAME_AGENT, value);
  }

}
