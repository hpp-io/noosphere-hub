package io.hpp.noosphere.hub.web.rest.vm;

import io.hpp.noosphere.hub.domain.enumeration.StatusCode;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Data;

@Data
@Schema
public class SearchAgentContainerVm {

    @Schema(description = "Container Name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String containerName;

    @Schema(description = "Agent Container Status Code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private StatusCode statusCode;
}
