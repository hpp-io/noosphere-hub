package io.hpp.noosphere.hub.security;

import io.hpp.noosphere.hub.service.UserService;
import io.hpp.noosphere.hub.service.dto.UserDTO;
import io.hpp.noosphere.hub.service.uil.CommonUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyAuthManager implements AuthenticationManager {

  private final Logger log = LoggerFactory.getLogger(ApiKeyAuthManager.class);

  private UserService userService;

  public ApiKeyAuthManager(UserService userService){
    this.userService = userService;
  }


  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String apiKey = (String) authentication.getPrincipal();
    if (!CommonUtils.isValid(apiKey)) {
      throw new BadCredentialsException("The API key was not found or not the expected value.");
    }
    try {
      UserDTO userDTO = userService.findByApiKey(apiKey, true);
      Set<String>  authorities = userDTO.getAuthorities();
      Collection<SimpleGrantedAuthority> authorityList = new ArrayList<>();
      authentication.getAuthorities().clear();
      for (String authority: authorities){
        authorityList.add(new SimpleGrantedAuthority(authority));
      }
     authentication = new ApiKeyAuthentication(apiKey, userDTO.getId(), authorityList);
//      authentication.getAuthorities().addAll(authorityList);
    } catch (Exception e) {
      log.error("failed to find user by api key " + apiKey, e);
      throw new BadCredentialsException("The API key was not found or not the expected value.");
    }
//    authentication.setAuthenticated(true);
    return authentication;
  }

}
