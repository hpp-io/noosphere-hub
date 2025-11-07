package io.hpp.noosphere.hub.web.rest.vm;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema
public class RegisterContainerVm {

    @Schema(description = "Container Name", requiredMode = RequiredMode.REQUIRED)
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
