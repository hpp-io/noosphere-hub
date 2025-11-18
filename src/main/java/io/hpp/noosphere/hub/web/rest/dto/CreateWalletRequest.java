package io.hpp.noosphere.hub.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonView;
import io.hpp.noosphere.hub.service.dto.JsonViewType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Data;

@Data
@Schema
public class CreateWalletRequest {

    @JsonView(JsonViewType.Update.class)
    @Schema(description = "Owner Address", requiredMode = RequiredMode.REQUIRED)
    private String ownerAddress;

}
