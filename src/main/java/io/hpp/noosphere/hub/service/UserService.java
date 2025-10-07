package io.hpp.noosphere.hub.service;

import static io.hpp.noosphere.hub.config.Constants.ATTRIBUTE_API_KEY;
import static io.hpp.noosphere.hub.config.Constants.ATTRIBUTE_IMAGE_URL;
import static io.hpp.noosphere.hub.config.Constants.ATTRIBUTE_LANG_KEY;

import io.hpp.noosphere.hub.config.ApplicationProperties;
import io.hpp.noosphere.hub.config.Constants;
import io.hpp.noosphere.hub.domain.Authority;
import io.hpp.noosphere.hub.domain.User;
import io.hpp.noosphere.hub.repository.AuthorityRepository;
import io.hpp.noosphere.hub.repository.UserRepository;
import io.hpp.noosphere.hub.security.SecurityUtils;
import io.hpp.noosphere.hub.service.dto.UserDTO;
import io.hpp.noosphere.hub.service.mapper.UserMapper;
import io.hpp.noosphere.hub.service.uil.CommonUtils;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

  private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  private final AuthorityRepository authorityRepository;

  private final CacheManager cacheManager;
  private final UserMapper userMapper;

  private final Keycloak keycloakAdminClient;
  private final ApplicationProperties applicationProperties;


  public UserService(UserRepository userRepository,
    AuthorityRepository authorityRepository,
    CacheManager cacheManager,
    UserMapper userMapper,
    Keycloak keycloakAdminClient,
    ApplicationProperties applicationProperties) {
    this.userRepository = userRepository;
    this.authorityRepository = authorityRepository;
    this.cacheManager = cacheManager;
    this.userMapper = userMapper;
    this.keycloakAdminClient = keycloakAdminClient;
    this.applicationProperties = applicationProperties;
  }

  private static User getUser(Map<String, Object> details) {
    User user = new User();
    Boolean activated = Boolean.TRUE;
    String sub = String.valueOf(details.get("sub"));
    String username = null;
    if (details.get("preferred_username") != null) {
      username = ((String) details.get("preferred_username")).toLowerCase();
    }
    // handle resource server JWT, where sub claim is email and uid is ID
    if (details.get("uid") != null) {
      user.setId((String) details.get("uid"));
      user.setLogin(sub);
    } else {
      user.setId(sub);
    }
    if (username != null) {
      user.setLogin(username);
    } else if (user.getLogin() == null) {
      user.setLogin(user.getId());
    }
    if (details.get("given_name") != null) {
      user.setFirstName((String) details.get("given_name"));
    } else if (details.get("name") != null) {
      user.setFirstName((String) details.get("name"));
    }
    if (details.get("family_name") != null) {
      user.setLastName((String) details.get("family_name"));
    }
    if (details.get("email_verified") != null) {
      activated = (Boolean) details.get("email_verified");
    }
    if (details.get("email") != null) {
      user.setEmail(((String) details.get("email")).toLowerCase());
    } else if (sub.contains("|") && (username != null && username.contains("@"))) {
      // special handling for Auth0
      user.setEmail(username);
    } else {
      user.setEmail(sub);
    }
    if (details.get("langKey") != null) {
      user.setLangKey((String) details.get("langKey"));
    } else if (details.get("locale") != null) {
      // trim off country code if it exists
      String locale = (String) details.get("locale");
      if (locale.contains("_")) {
        locale = locale.substring(0, locale.indexOf('_'));
      } else if (locale.contains("-")) {
        locale = locale.substring(0, locale.indexOf('-'));
      }
      user.setLangKey(locale.toLowerCase());
    } else {
      // set langKey to default if not specified by IdP
      user.setLangKey(Constants.DEFAULT_LANGUAGE);
    }
    if (details.get("picture") != null) {
      user.setImageUrl((String) details.get("picture"));
    }
    if (details.get("api_key") != null) {
      user.setApiKey((String) details.get("api_key"));
    }
    user.setActivated(activated);
    return user;
  }

  public void createUser(UserDTO userDTO) {
    User user = userMapper.userDTOToUser(userDTO);
    userRepository.save(user);
    this.clearUserCaches(user);
    LOG.debug("Created User: {}", user);
  }

  /**
   * Update basic information (first name, last name, email, language) for the current user.
   *
   * @param firstName first name of user.
   * @param lastName  last name of user.
   * @param email     email id of user.
   * @param langKey   language key.
   * @param imageUrl  image URL of user.
   */
  public void updateUser(String firstName, String lastName, String email, String apiKey, String langKey, String imageUrl) {
    SecurityUtils.getCurrentUserLogin()
      .flatMap(userRepository::findOneByEmail)
      .ifPresent(user -> {
        user.setName(CommonUtils.buildFullName(langKey, firstName, lastName));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        if (email != null) {
          user.setEmail(email.toLowerCase());
        }
        if (apiKey != null) {
          user.setApiKey(apiKey.trim());
        }
        user.setLangKey(langKey);
        user.setImageUrl(imageUrl);
        userRepository.save(user);
        this.clearUserCaches(user);
        LOG.debug("Changed Information for User: {}", user);
      });
  }

  @Transactional(readOnly = true)
  public List<String> getAuthorities() {
    return authorityRepository.findAll().stream().map(Authority::getName).toList();
  }

  private User syncUserWithIdP(Map<String, Object> details, User user) {
    // save authorities in to sync user roles/groups between IdP and JHipster's local database
    Collection<String> dbAuthorities = getAuthorities();
    Collection<String> userAuthorities = user.getAuthorities().stream().map(Authority::getName).toList();
    for (String authority : userAuthorities) {
      if (!dbAuthorities.contains(authority)) {
        LOG.debug("Saving authority '{}' in local database", authority);
        Authority authorityToSave = new Authority();
        authorityToSave.setName(authority);
        authorityRepository.save(authorityToSave);
      }
    }
    // save account in to sync users between IdP and JHipster's local database
    Optional<User> existingUser = userRepository.findOneByEmail(user.getEmail());
    if (existingUser.isPresent()) {
      // if IdP sends last updated information, use it to determine if an update should happen
      if (details.get("updated_at") != null) {
        Instant dbModifiedDate = existingUser.orElseThrow().getLastModifiedDate();
        Instant idpModifiedDate;
        if (details.get("updated_at") instanceof Instant) {
          idpModifiedDate = (Instant) details.get("updated_at");
        } else {
          idpModifiedDate = Instant.ofEpochSecond((Integer) details.get("updated_at"));
        }
        if (idpModifiedDate.isAfter(dbModifiedDate)) {
          LOG.debug("Updating user '{}' in local database", user.getLogin());
          updateUser(user.getFirstName(), user.getLastName(), user.getEmail(), user.getApiKey(), user.getLangKey(), user.getImageUrl());
        }
        // no last updated info, blindly update
      } else {
        LOG.debug("Updating user '{}' in local database", user.getLogin());
        updateUser(user.getFirstName(), user.getLastName(), user.getEmail(), user.getApiKey(), user.getLangKey(), user.getImageUrl());
      }
    } else {
      LOG.debug("Saving user '{}' in local database", user.getLogin());
      userRepository.save(user);
      this.clearUserCaches(user);
    }
    return user;
  }

  /**
   * Returns the user from an OAuth 2.0 login or resource server with JWT. Synchronizes the user in the local repository.
   *
   * @param authToken the authentication token.
   * @return the user from the authentication.
   */
  public UserDTO getUserFromAuthentication(AbstractAuthenticationToken authToken) {
    Map<String, Object> attributes;
    if (authToken instanceof OAuth2AuthenticationToken) {
      attributes = ((OAuth2AuthenticationToken) authToken).getPrincipal().getAttributes();
    } else if (authToken instanceof JwtAuthenticationToken) {
      attributes = ((JwtAuthenticationToken) authToken).getTokenAttributes();
    } else {
      throw new IllegalArgumentException("AuthenticationToken is not OAuth2 or JWT!");
    }
    User user = getUser(attributes);
    user.setAuthorities(
      authToken
        .getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .map(authority -> {
          Authority auth = new Authority();
          auth.setName(authority);
          return auth;
        })
        .collect(Collectors.toSet())
    );

    return new UserDTO(syncUserWithIdP(attributes, user));
  }

  private void clearUserCaches(User user) {
    Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE)).evictIfPresent(user.getEmail());
    if (user.getApiKey() != null) {
      Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_API_KEY_CACHE)).evictIfPresent(user.getApiKey());
    }
  }

  @Transactional(readOnly = true)
  public Optional<User> findOptionalByApiKey(String apiKey, Boolean activated) {
    return userRepository.findOneByApiKey(apiKey, activated);
  }

  public UserRepresentation findKeycloakUserByApiKey(String apiKey) {
    List<UserRepresentation> list = keycloakAdminClient
      .realm(applicationProperties.getKeycloak().getRealmId())
      .users()
      .searchByAttributes(ATTRIBUTE_API_KEY + ":" + apiKey, true);
    if (list != null && list.size() == 1) {
      return list.get(0);
    }
    return null;
  }

  public void populateAuthoritiesFromKeycloakUser(UserDTO userDTO) {
    List<GroupRepresentation> list = keycloakAdminClient
      .realm(applicationProperties.getKeycloak().getRealmId())
      .users()
      .get(userDTO.getId()).groups();
    if (list != null && !list.isEmpty()) {
      for (GroupRepresentation group : list) {
        if (Constants.KEYCLOAK_GROUP_ADMIN.equalsIgnoreCase(group.getName())) {
          userDTO.getAuthorities().add(Constants.KEYCLOAK_ROLE_ADMIN);
        } else if (Constants.KEYCLOAK_GROUP_USER.equalsIgnoreCase(group.getName())) {
          userDTO.getAuthorities().add(Constants.KEYCLOAK_ROLE_USER);
        }
      }
    }
  }

  public String getAttributeValue(UserRepresentation user, String attributeName) {
    String value = null;
    if (user != null) {
      List<String> list = user.getAttributes().get(attributeName);
      if (list != null && !list.isEmpty()) {
        value = list.get(0);
      }
    }
    return value;
  }

  public UserDTO createUserFromKeycloakUser(UserRepresentation keycloakUser) {
    UserDTO userDTO = null;
    if (keycloakUser != null) {
      userDTO = new UserDTO();
      userDTO.setId(keycloakUser.getId());
      userDTO.setLogin(keycloakUser.getUsername());
      userDTO.setLangKey(getAttributeValue(keycloakUser, ATTRIBUTE_LANG_KEY));
      userDTO.setFirstName(keycloakUser.getFirstName());
      userDTO.setLastName(keycloakUser.getLastName());
      userDTO.setName(CommonUtils.buildFullName(userDTO.getLangKey(), keycloakUser.getFirstName(), keycloakUser.getLastName()));
      userDTO.setEmail(keycloakUser.getEmail());
      userDTO.setImageUrl(getAttributeValue(keycloakUser, ATTRIBUTE_IMAGE_URL));
      userDTO.setApiKey(getAttributeValue(keycloakUser, ATTRIBUTE_API_KEY));
      userDTO.setActivated(keycloakUser.isEnabled());
      populateAuthoritiesFromKeycloakUser(userDTO);
    }
    return userDTO;
  }

  public UserDTO findByApiKey(String apiKey, Boolean activated) {
    Optional<User> optionalUser = this.findOptionalByApiKey(apiKey, activated);
    if (optionalUser.isEmpty()) {
      UserRepresentation keycloakUser = findKeycloakUserByApiKey(apiKey);
      if (keycloakUser != null) {
        UserDTO userDTO = createUserFromKeycloakUser(keycloakUser);
        this.createUser(userDTO);
        return userDTO;
      }
    }
    return optionalUser.map(userMapper::userToUserDTO).orElse(null);
  }
}
