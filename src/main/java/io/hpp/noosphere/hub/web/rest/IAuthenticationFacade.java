package io.hpp.noosphere.hub.web.rest;

import org.springframework.security.core.Authentication;

public interface IAuthenticationFacade {

  Authentication getAuthentication();

  String getUserId();
}
