package io.hpp.noosphere.hub.domain;

import java.util.UUID;

public class AgentStatusTestSamples {

    public static AgentStatus getAgentStatusSample1() {
        return new AgentStatus().id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"));
    }

    public static AgentStatus getAgentStatusSample2() {
        return new AgentStatus().id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"));
    }

    public static AgentStatus getAgentStatusRandomSampleGenerator() {
        return new AgentStatus().id(UUID.randomUUID());
    }
}
