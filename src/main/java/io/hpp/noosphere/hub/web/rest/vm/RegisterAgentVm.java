package io.hpp.noosphere.hub.web.rest.vm;

import io.hpp.noosphere.common.domain.enumeration.StatusCode;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema
public class RegisterAgentVm {

  @Schema(description = "Agent Name", requiredMode = RequiredMode.NOT_REQUIRED)
  private String name;

  @Schema(description = "API Key", requiredMode = RequiredMode.REQUIRED)
  @NotNull
  private String apiKey;

  @Schema(description = "Wallet Address", requiredMode = RequiredMode.REQUIRED)
  @NotNull
  private String walletAddress;

  @Schema(description = "Email", requiredMode = RequiredMode.REQUIRED)
  @NotNull
  private String email;
}
