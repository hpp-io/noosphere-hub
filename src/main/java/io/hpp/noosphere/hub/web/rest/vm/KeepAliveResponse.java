package io.hpp.noosphere.hub.web.rest.vm;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class KeepAliveResponse {

    @Schema(description = "Status Code", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer statusCode;
}
