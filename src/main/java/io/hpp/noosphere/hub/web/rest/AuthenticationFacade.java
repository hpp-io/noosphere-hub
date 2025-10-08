package io.hpp.noosphere.hub.web.rest;

import io.hpp.noosphere.hub.security.ApiKeyAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacade implements IAuthenticationFacade {

  private static final Logger LOG = LoggerFactory.getLogger(AuthenticationFacade.class);

  @Override
  public Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  @Override
  public String getUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof JwtAuthenticationToken authentication1) {
      return authentication1.getTokenAttributes().get("sub") != null ? authentication1.getTokenAttributes().get("sub").toString() : null;
    } else if (authentication instanceof ApiKeyAuthentication authentication1) {
      return authentication1.getUserId();
    } else {
      return authentication.getName();
    }
  }

}
