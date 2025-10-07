package io.hpp.noosphere.hub.web.rest.vm;

import io.hpp.noosphere.hub.domain.enumeration.StatusCode;
import lombok.Data;

@Data
public class SearchAgentVm {

    private String name;
    private StatusCode statusCode;
    private String createdByUserId;
}
