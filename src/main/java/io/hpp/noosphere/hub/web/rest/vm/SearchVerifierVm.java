package io.hpp.noosphere.hub.web.rest.vm;

import io.hpp.noosphere.common.domain.enumeration.StatusCode;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Data;

@Data
@Schema
public class SearchVerifierVm {

  @Schema(description = "Validator Name", requiredMode = RequiredMode.NOT_REQUIRED)
  private String name;

  @Schema(description = "Search Text", requiredMode = RequiredMode.NOT_REQUIRED)
  private String searchText;

  @Schema(description = "Wallet Address", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private String walletAddress;

  @Schema(description = "Verifier Address", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private String verifierAddress;

  @Schema(description = "Validator Status Code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private StatusCode statusCode;

  @Schema(description = "Created By User Id", requiredMode = RequiredMode.NOT_REQUIRED)
  private String createdByUserId;
}
