package io.hpp.noosphere.hub.service.dto;

import com.fasterxml.jackson.annotation.JsonView;
import io.hpp.noosphere.hub.domain.Authority;
import io.hpp.noosphere.hub.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO representing a user, with only the public attributes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema
public class UserDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  @JsonView(JsonViewType.Shallow.class)
  private String id;
  @JsonView(JsonViewType.Shallow.class)
  private String name;
  @JsonView(JsonViewType.Full.class)
  private String firstName;
  @JsonView(JsonViewType.Full.class)
  private String lastName;
  @JsonView(JsonViewType.Full.class)
  private String login;
  @JsonView(JsonViewType.Shallow.class)
  private String email;
  @JsonView(JsonViewType.Full.class)
  private String imageUrl;
  @JsonView(JsonViewType.Full.class)
  private String apiKey;
  @JsonView(JsonViewType.Shallow.class)
  private String langKey;
  @JsonView(JsonViewType.Full.class)
  private Boolean activated;
  @JsonView(JsonViewType.Full.class)
  private Set<String> authorities = new HashSet<>();

  public UserDTO(User user) {
    this.id = user.getId();
    this.login = user.getLogin();
    this.name = user.getName();
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
    this.email = user.getEmail();
    this.imageUrl = user.getImageUrl();
    this.apiKey = user.getApiKey();
    this.langKey = user.getLangKey();
    this.activated = user.isActivated();
    this.authorities = user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet());
  }
}
