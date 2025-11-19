package io.hpp.noosphere.hub.security;

import static io.hpp.noosphere.hub.config.Constants.HTTP_HEADER_API_KEY;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
public class ApiKeyAuthFilter extends GenericFilterBean {

  private ApiKeyAuthManager manager;

  public ApiKeyAuthFilter(ApiKeyAuthManager manager) {
    this.manager = manager;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    String apiKey = httpRequest.getHeader(HTTP_HEADER_API_KEY);

    if (apiKey != null) {
      ApiKeyAuthentication apiKeyAuthentication = new ApiKeyAuthentication(apiKey, null, null);
      SecurityContextHolder.getContext().setAuthentication(manager.authenticate(apiKeyAuthentication));
    }

    chain.doFilter(request, response);
  }
}
