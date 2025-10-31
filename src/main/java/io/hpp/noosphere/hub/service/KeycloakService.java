package io.hpp.noosphere.hub.service;

import static io.hpp.noosphere.hub.config.Constants.PROPERTY_NAME_API_KEY;
import static io.hpp.noosphere.hub.config.Constants.PROPERTY_NAME_IMAGE_URL;
import static io.hpp.noosphere.hub.config.Constants.PROPERTY_NAME_LANG_KEY;
import static io.hpp.noosphere.hub.config.Constants.PROPERTY_NAME_WALLET_ADDRESS;
import static io.hpp.noosphere.hub.config.KeycloakConstants.KEYCLOAK_GROUP_ADMIN;
import static io.hpp.noosphere.hub.config.KeycloakConstants.KEYCLOAK_GROUP_USER;
import static io.hpp.noosphere.hub.config.KeycloakConstants.KEYCLOAK_ROLE_ADMIN;
import static io.hpp.noosphere.hub.config.KeycloakConstants.KEYCLOAK_ROLE_USER;
import static io.hpp.noosphere.hub.domain.User_.email;

import io.hpp.noosphere.hub.config.ApplicationProperties;
import io.hpp.noosphere.hub.exception.GroupNotFoundException;
import io.hpp.noosphere.hub.exception.UserNotFoundException;
import io.hpp.noosphere.hub.service.dto.UserDTO;
import io.hpp.noosphere.hub.service.uil.CommonUtils;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class KeycloakService {

  private final Keycloak keycloakAdminClient;
  private final ApplicationProperties applicationProperties;

  public KeycloakService(
    Keycloak keycloakAdminClient,
    ApplicationProperties applicationProperties
  ) {
    this.keycloakAdminClient = keycloakAdminClient;
    this.applicationProperties = applicationProperties;
  }

  public UserResource getUserResource(String realmId, String id) {
    return keycloakAdminClient.realm(realmId).users().get(id);
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

  public void setAttributeValue(UserRepresentation user, String attributeName, String value) {
    if (user != null && CommonUtils.isValid(attributeName)) {
      Map<String, List<String>> attributes = new HashMap<>();
      Map<String, List<String>> existingAttributes = user.getAttributes();
      if (existingAttributes != null) {
        attributes.putAll(existingAttributes);
      }
      user.setAttributes(attributes);
      if (CommonUtils.isValid(value)) {
        List<String> list = List.of(value);
        attributes.put(attributeName, list);
      } else {
        attributes.remove(attributeName);
      }
    }
  }

  public void populateKeycloakUser(UserRepresentation keycloakUser, UserDTO userDTO) {
    keycloakUser.setEmail(userDTO.getEmail());
    keycloakUser.setEnabled(true);
    keycloakUser.setEmailVerified(false);
    keycloakUser.setFirstName(userDTO.getFirstName());
    keycloakUser.setLastName(userDTO.getLastName());
    keycloakUser.setUsername(userDTO.getEmail());
    setAttributeValue(keycloakUser, PROPERTY_NAME_LANG_KEY, userDTO.getLangKey());
    setAttributeValue(keycloakUser, PROPERTY_NAME_API_KEY, userDTO.getApiKey());
    setAttributeValue(keycloakUser, PROPERTY_NAME_WALLET_ADDRESS, userDTO.getWalletAddress());
  }

  public void populateAuthoritiesFromKeycloakUser(UserDTO userDTO) {
    List<GroupRepresentation> list = keycloakAdminClient
      .realm(applicationProperties.getKeycloak().getRealmId())
      .users()
      .get(userDTO.getId())
      .groups();
    if (list != null && !list.isEmpty()) {
      for (GroupRepresentation group : list) {
        if (KEYCLOAK_GROUP_ADMIN.equalsIgnoreCase(group.getName())) {
          userDTO.getAuthorities().add(KEYCLOAK_ROLE_ADMIN);
        } else if (KEYCLOAK_GROUP_USER.equalsIgnoreCase(group.getName())) {
          userDTO.getAuthorities().add(KEYCLOAK_ROLE_USER);
        }
      }
    }
  }

  public UserRepresentation findKeycloakUserByApiKey(String apiKey) {
    List<UserRepresentation> list = keycloakAdminClient
      .realm(applicationProperties.getKeycloak().getRealmId())
      .users()
      .searchByAttributes(PROPERTY_NAME_API_KEY + ":" + apiKey, true);
    if (list != null && list.size() == 1) {
      return list.get(0);
    }
    return null;
  }

  public UserRepresentation findKeycloakUserByEmail(String email) {
    List<UserRepresentation> list = keycloakAdminClient
      .realm(applicationProperties.getKeycloak().getRealmId())
      .users()
      .search(email);
    if (list != null && list.size() == 1) {
      return list.get(0);
    }
    return null;
  }

  public GroupRepresentation findOneGroupByName(String realmId, String groupName) {
    List<GroupRepresentation> retrieveGroupList = keycloakAdminClient.realm(realmId).groups().groups(groupName, 0, 10, false);
    if (!retrieveGroupList.isEmpty()) {
      for (GroupRepresentation groupRepresentation : retrieveGroupList) {
        if (groupRepresentation.getName().equalsIgnoreCase(groupName)) {
          return groupRepresentation;
        }
      }
      return null;
    } else {
      return null;
    }
  }


  public void joinOrLeaveUserGroup(String realmId, String userId, String groupName, boolean isJoin) throws UserNotFoundException, GroupNotFoundException {
    UserResource userResource = getUserResource(realmId, userId);
    GroupRepresentation retrievedGroup = findOneGroupByName(realmId, groupName);
    if (userResource != null && retrievedGroup != null) {
      if (isJoin) {
        userResource.joinGroup(retrievedGroup.getId());
      } else {
        userResource.leaveGroup(retrievedGroup.getId());
      }
    } else {
      if (userResource != null) {
        throw new UserNotFoundException(userId);
      } else if (retrievedGroup != null) {
        throw new GroupNotFoundException(groupName);
      }
    }
  }

  public UserRepresentation createKeycloakUser(UserDTO userDTO) {
    UserRepresentation userRepresentation = new UserRepresentation();
    this.populateKeycloakUser(userRepresentation, userDTO);
    Response response = keycloakAdminClient
      .realm(applicationProperties.getKeycloak().getRealmId())
      .users()
      .create(userRepresentation);
    if (response != null && HttpStatus.CREATED.value() == response.getStatus()) {
      userRepresentation = this.findKeycloakUserByEmail(userDTO.getEmail());
    }
    return userRepresentation;
  }

  public void updateKeycloakUser(String userId, String email,
    String newFirstName, String newLastName, String newEmail, String newApiKey, String newLangKey, String newImageUrl, String newWalletAddress) {
    UserRepresentation userRepresentation = this.findKeycloakUserByEmail(email);
    if (CommonUtils.isValid(newFirstName)) {
      userRepresentation.setFirstName(newFirstName);
    }
    if (CommonUtils.isValid(newLastName)) {
      userRepresentation.setLastName(newLastName);
    }
    if (CommonUtils.isValid(newEmail)) {
      userRepresentation.setEmail(newEmail);
    }
    if (CommonUtils.isValid(newApiKey)) {
      setAttributeValue(userRepresentation, PROPERTY_NAME_API_KEY, newApiKey);
    }
    if (CommonUtils.isValid(newLangKey)) {
      setAttributeValue(userRepresentation, PROPERTY_NAME_LANG_KEY, newLangKey);
    }
    if (CommonUtils.isValid(newImageUrl)) {
      setAttributeValue(userRepresentation, PROPERTY_NAME_IMAGE_URL, newImageUrl);
    }
    if (CommonUtils.isValid(newWalletAddress)) {
      setAttributeValue(userRepresentation, PROPERTY_NAME_WALLET_ADDRESS, newWalletAddress);
    }

    keycloakAdminClient
      .realm(applicationProperties.getKeycloak().getRealmId())
      .users()
      .get(userId).update(userRepresentation);
  }
}
