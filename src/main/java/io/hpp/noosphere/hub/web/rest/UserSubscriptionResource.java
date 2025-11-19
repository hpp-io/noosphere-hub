package io.hpp.noosphere.hub.web.rest;

import com.fasterxml.jackson.annotation.JsonView;
import io.hpp.noosphere.hub.config.OpenApiConfiguration;
import io.hpp.noosphere.hub.service.UserService;
import io.hpp.noosphere.hub.service.UserSubscriptionService;
import io.hpp.noosphere.hub.service.blockchain.dto.SubscriptionDTO;
import io.hpp.noosphere.hub.service.dto.JsonViewType;
import io.hpp.noosphere.hub.service.dto.UserSubscriptionDTO;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user-subscriptions")
@Tag(
  name = "User Subscription",
  description = "User Subscription Controller",
  extensions = {@Extension(properties = {@ExtensionProperty(name = OpenApiConfiguration.TAG_ORDER, value = "1")})}
)
public class UserSubscriptionResource {

  private static final Logger LOG = LoggerFactory.getLogger(UserSubscriptionResource.class);

  private static final String ENTITY_NAME = "userSubscription";
  private final IAuthenticationFacade authenticationFacade;
  private final UserService userService;
  private final UserSubscriptionService userSubscriptionService;

  @Value("${jhipster.clientApp.name}")
  private String applicationName;

  public UserSubscriptionResource(
    UserService userService,
    UserSubscriptionService userSubscriptionService,
    IAuthenticationFacade authenticationFacade) {
    this.authenticationFacade = authenticationFacade;
    this.userService = userService;
    this.userSubscriptionService = userSubscriptionService;
  }

  @PostMapping("")
  @JsonView(JsonViewType.Update.class)
  public ResponseEntity<List<UserSubscriptionDTO>> createUserSubscriptionList(@RequestBody List<SubscriptionDTO> subscriptionDTOList)
    throws URISyntaxException {
    LOG.debug("REST request to create UserSubscription List : {}", subscriptionDTOList.size());
    Instant now = Instant.now();
    List<UserSubscriptionDTO> userSubscriptionDTOList = userSubscriptionService.create(userService, subscriptionDTOList, now);
    return ResponseEntity.created(new URI("/api/user-subscriptions"))
      .body(userSubscriptionDTOList);
  }
}
