package io.hpp.noosphere.hub.web.rest;

import com.fasterxml.jackson.annotation.JsonView;
import io.hpp.noosphere.hub.config.OpenApiConfiguration;
import io.hpp.noosphere.hub.service.UserService;
import io.hpp.noosphere.hub.service.dto.JsonViewType;
import io.hpp.noosphere.hub.service.dto.UserDTO;
import io.hpp.noosphere.hub.web.rest.vm.UpdateWalletVm;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Tag(
  name = "User",
  description = "User Controller",
  extensions = {@Extension(properties = {@ExtensionProperty(name = OpenApiConfiguration.TAG_ORDER, value = "1")})}
)
public class UserResource {

  private static final Logger LOG = LoggerFactory.getLogger(UserResource.class);

  private static final String ENTITY_NAME = "user";
  private final IAuthenticationFacade authenticationFacade;
  private final UserService userService;

  @Value("${jhipster.clientApp.name}")
  private String applicationName;

  public UserResource(UserService userService, IAuthenticationFacade authenticationFacade) {
    this.authenticationFacade = authenticationFacade;
    this.userService = userService;
  }

  @JsonView(JsonViewType.Shallow.class)
  @PostMapping("/mine/wallet")
  public ResponseEntity<String> createWallet(@Valid @RequestBody UpdateWalletVm updateWalletVm) {
    LOG.debug("REST request to create wallet");
    Instant now = Instant.now();
    String walletAddress = userService.createAndUpdateWallet(authenticationFacade.getUserId(), updateWalletVm.getOwnerAddress(), now);
    return ResponseEntity.ok().body(walletAddress);
  }

  @JsonView(JsonViewType.Shallow.class)
  @PutMapping("/mine/wallet")
  public ResponseEntity<String> updateWallet(@Valid @RequestBody UpdateWalletVm updateWalletVm) {
    LOG.debug("REST request to update wallet");
    Instant now = Instant.now();
    String walletAddress = userService.updateWithNewWallet(authenticationFacade.getUserId(), updateWalletVm.getOwnerAddress(), now);
    return ResponseEntity.ok().body(walletAddress);
  }

  @JsonView(JsonViewType.Shallow.class)
  @GetMapping("/mine/wallet")
  public ResponseEntity<String> getWallet() {
    LOG.debug("REST request to get wallet");
    UserDTO userDTO = userService.findById(authenticationFacade.getUserId());
    String walletAddress = userDTO.getWalletAddress();
    return ResponseEntity.ok().body(walletAddress);
  }

  @JsonView(JsonViewType.Shallow.class)
  @PostMapping("/mine/api-key")
  public ResponseEntity<String> createApiKey(@RequestBody UserDTO userDTO) {
    LOG.debug("REST request to create apiKey");
    Instant now = Instant.now();
    String apiKey = userService.createAndUpdateApiKey(authenticationFacade.getUserId(), now);
    return ResponseEntity.ok().body(apiKey);
  }

  @JsonView(JsonViewType.Shallow.class)
  @GetMapping("/mine/api-key")
  public ResponseEntity<String> getApiKey() {
    LOG.debug("REST request to get apiKey");
    UserDTO userDTO = userService.findById(authenticationFacade.getUserId());
    String apiKey = userDTO.getApiKey();
    return ResponseEntity.ok().body(apiKey);
  }

  @JsonView(JsonViewType.Update.class)
  @GetMapping("/profile")
  public ResponseEntity<UserDTO> getUserProfile() {
    LOG.debug("REST request to get User Profile");
    UserDTO userDTO = userService.findById(authenticationFacade.getUserId());
    return ResponseEntity.ok().body(userDTO);
  }

  @JsonView(JsonViewType.Update.class)
  @PutMapping("/profile")
  public ResponseEntity<Void> updateUserProfile(@RequestBody UserDTO userDTO) {
    LOG.debug("REST request to update User Profile");
    Instant now = Instant.now();
    userService.updateUserProfile(
      authenticationFacade.getUserId(),
      userDTO.getFirstName(),
      userDTO.getLastName(),
      userDTO.getLangKey(),
      userDTO.getImageUrl(),
      now
    );
    return ResponseEntity.ok().build();
  }

  @GetMapping("/from-api-key")
  public ResponseEntity<UserDTO> getUserFromApiKey(@RequestHeader("X-API-KEY") String apiKey) {
    LOG.debug("REST request to get user from api key");
    UserDTO userDTO = userService.findByApiKey(apiKey, Boolean.TRUE);
    return ResponseEntity.ok().body(userDTO);
  }
}
