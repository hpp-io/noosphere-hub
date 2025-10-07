package io.hpp.noosphere.hub.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakAdminClientConfig {
  @Value("${application.keycloak.auth-url}")
  private String keycloakServerUrl;

  @Value("${application.keycloak.realm-id}")
  private String realmId;

  @Value("${application.keycloak.admin-client-id}")
  private String adminClientId;

  @Value("${application.keycloak.admin-client-secret}")
  private String adminClientSecret;

  @Bean
  public Keycloak keycloakAdminClient() {
    return KeycloakBuilder.builder()
      .serverUrl(keycloakServerUrl)
      .realm(realmId)
      .clientId(adminClientId)
      .clientSecret(adminClientSecret)
      .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
      .build();
  }
}
