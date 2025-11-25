package io.hpp.noosphere.hub.web.rest;

import com.fasterxml.jackson.annotation.JsonView;
import io.hpp.noosphere.common.service.blockchain.WalletService;
import io.hpp.noosphere.common.service.util.CommonUtils;
import io.hpp.noosphere.hub.config.OpenApiConfiguration;
import io.hpp.noosphere.hub.service.UserService;
import io.hpp.noosphere.hub.service.dto.JsonViewType;
import io.hpp.noosphere.hub.web.rest.dto.CreateWalletRequest;
import io.hpp.noosphere.hub.web.rest.errors.BadRequestAlertException;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wallets")
@Tag(
  name = "Wallet",
  description = "Wallet Controller",
  extensions = { @Extension(properties = { @ExtensionProperty(name = OpenApiConfiguration.TAG_ORDER, value = "1") }) }
)
public class WalletResource {

  private static final Logger LOG = LoggerFactory.getLogger(WalletResource.class);

  private static final String ENTITY_NAME = "noosphereWallet";
  private final IAuthenticationFacade authenticationFacade;
  private final WalletService walletService;
  private final UserService userService;

  @Value("${jhipster.clientApp.name}")
  private String applicationName;

  public WalletResource(WalletService walletService, UserService userService, IAuthenticationFacade authenticationFacade) {
    this.authenticationFacade = authenticationFacade;
    this.walletService = walletService;
    this.userService = userService;
  }

  @PostMapping("")
  @JsonView(JsonViewType.Update.class)
  public ResponseEntity<String> createWallet(@RequestBody CreateWalletRequest createWalletRequest) {
    LOG.debug("REST request to create Wallet : {}", createWalletRequest);
    if (!CommonUtils.isValid(createWalletRequest.getOwnerAddress())) {
      throw new BadRequestAlertException("An owner address cannot be null.", ENTITY_NAME, "isnull");
    }
    Instant now = Instant.now();
    String walletAddress = walletService.createWallet(createWalletRequest.getOwnerAddress());
    return ResponseEntity.ok().body(walletAddress);
  }
}
