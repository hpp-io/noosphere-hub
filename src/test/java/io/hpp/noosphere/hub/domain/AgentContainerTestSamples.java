package io.hpp.noosphere.hub.domain;

import java.util.UUID;

public class AgentContainerTestSamples {

    public static AgentContainer getAgentContainerSample1() {
        return new AgentContainer().id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa")).statusCode("statusCode1");
    }

    public static AgentContainer getAgentContainerSample2() {
        return new AgentContainer().id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367")).statusCode("statusCode2");
    }

    public static AgentContainer getAgentContainerRandomSampleGenerator() {
        return new AgentContainer().id(UUID.randomUUID()).statusCode(UUID.randomUUID().toString());
    }
}
