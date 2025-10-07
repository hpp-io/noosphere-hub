package io.hpp.noosphere.hub.web.rest.vm;

import io.hpp.noosphere.hub.domain.enumeration.StatusCode;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Data;

@Data
@Schema
public class SearchContainerVm {

    @Schema(description = "Container Name", requiredMode = RequiredMode.NOT_REQUIRED)
    private String name;
    @Schema(description = "Container Status Code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private StatusCode statusCode;
    @Schema(description = "Created By User Id", requiredMode = RequiredMode.NOT_REQUIRED)
    private String createdByUserId;
}
