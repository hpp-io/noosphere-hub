package io.hpp.noosphere.hub.security;

import java.util.Collection;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class WalletApiKeyAuthentication extends AbstractAuthenticationToken {

  @Getter
  private final String apiKey;
  private final String walletAddress;
  @Getter
  private final String email;
  @Getter
  private final String userId;
  public WalletApiKeyAuthentication(String apiKey, String walletAddress, String email, String userId, Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.apiKey = apiKey;
    this.walletAddress = walletAddress;
    this.email = email;
    this.userId = userId;
    setAuthenticated(true);
  }

  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public Object getPrincipal() {
    return apiKey;
  }

  @Override
  public String getName() {
    return userId;
  }

}
