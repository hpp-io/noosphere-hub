package io.hpp.noosphere.hub.web.rest.vm;

import io.hpp.noosphere.common.domain.enumeration.StatusCode;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.util.UUID;
import lombok.Data;

@Data
@Schema
public class SearchAgentRequestVm {

  @Schema(description = "Agent Name", requiredMode = RequiredMode.NOT_REQUIRED)
  private String agentName;

  @Schema(description = "Container Id", requiredMode = RequiredMode.NOT_REQUIRED)
  private UUID containerId;

  @Schema(description = "Agent Id", requiredMode = RequiredMode.NOT_REQUIRED)
  private UUID agentId;

  @Schema(description = "Status Code", requiredMode = RequiredMode.NOT_REQUIRED)
  private StatusCode statusCode;
}
