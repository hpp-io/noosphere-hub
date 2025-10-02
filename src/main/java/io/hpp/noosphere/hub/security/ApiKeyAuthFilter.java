package io.hpp.noosphere.hub.security;


import static io.hpp.noosphere.hub.config.Constants.HTTP_HEADER_API_KEY;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyAuthFilter extends AbstractPreAuthenticatedProcessingFilter {

  @Autowired
  public ApiKeyAuthFilter(ApiKeyAuthManager manager) {
    this.setAuthenticationManager(manager);
  }

  @Override
  protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
    return request.getHeader(HTTP_HEADER_API_KEY);
  }

  @Override
  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
    return "N/A";
  }

}
