package io.hpp.noosphere.hub.service.dto;

import io.hpp.noosphere.hub.domain.Authority;
import io.hpp.noosphere.hub.domain.User;
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
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String name;

    private String login;
    private String email;
    private String imageUrl;
    private String apiKey;
    private String langKey;
    private Boolean activated;

    private Set<String> authorities = new HashSet<>();

    public UserDTO(User user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.name = user.getName();
        this.email = user.getEmail();
        this.imageUrl = user.getImageUrl();
        this.apiKey = user.getApiKey();
        this.langKey = user.getLangKey();
        this.activated = user.isActivated();
        this.authorities = user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet());
    }
}
