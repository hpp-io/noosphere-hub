package io.hpp.noosphere.hub.web.rest.vm;

import io.hpp.noosphere.hub.domain.enumeration.StatusCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema

public class SearchAgentStatusVm {

    @Schema(description = "Agent Name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String agentName;
    @Schema(description = "Agent Status Code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private StatusCode agentStatusCode;
}
