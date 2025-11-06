package io.hpp.noosphere.hub.web.rest.vm;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class KeepAliveResponse {

    @Schema(description = "Count", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long count;
}
