package io.hpp.noosphere.hub.security;

import io.hpp.noosphere.hub.service.UserService;
import io.hpp.noosphere.hub.service.dto.UserDTO;
import io.hpp.noosphere.hub.service.uil.CommonUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@Primary
public class WalletApiKeyAuthManager implements AuthenticationManager {

  private final Logger log = LoggerFactory.getLogger(WalletApiKeyAuthManager.class);

  private UserService userService;

  public WalletApiKeyAuthManager(UserService userService) {
    this.userService = userService;
  }


  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String apiKey = (String) authentication.getPrincipal();
    if (!CommonUtils.isValid(apiKey)) {
      throw new BadCredentialsException("The wallet address was not found or not the expected value.");
    }
    try {
      Pair<String, String> pair = CommonUtils.splitApiKey(apiKey);
      if (!CommonUtils.isValid(pair.getLeft()) || !CommonUtils.isValid(pair.getRight())) {
        throw new BadCredentialsException("The wallet address was not found or not the expected value.");
      }
      UserDTO userDTO = userService.findOneByWalletAddress(pair.getLeft(), pair.getRight(), true);
      Set<String> authorities = userDTO.getAuthorities();
      Collection<SimpleGrantedAuthority> authorityList = new ArrayList<>();
      authentication.getAuthorities().clear();
      for (String authority : authorities) {
        authorityList.add(new SimpleGrantedAuthority(authority));
      }
      authentication = new WalletApiKeyAuthentication(apiKey, pair.getLeft(), pair.getRight(), userDTO.getId(), authorityList);
//      authentication.getAuthorities().addAll(authorityList);
    } catch (Exception e) {
      log.error("failed to find user by api key " + apiKey, e);
      throw new BadCredentialsException("The API key was not found or not the expected value.");
    }
//    authentication.setAuthenticated(true);
    return authentication;
  }

}
