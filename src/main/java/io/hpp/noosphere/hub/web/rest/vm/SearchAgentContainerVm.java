package io.hpp.noosphere.hub.web.rest.vm;

import io.hpp.noosphere.hub.domain.enumeration.StatusCode;
import lombok.Data;

@Data
public class SearchAgentContainerVm {

    private String agentName;
    private String containerName;
    private StatusCode statusCode;
}
