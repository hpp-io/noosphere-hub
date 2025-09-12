package io.hpp.noosphere.hub.domain;

import java.util.UUID;

public class AgentTestSamples {

    public static Agent getAgentSample1() {
        return new Agent()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .name("name1")
            .apiUrl("apiUrl1")
            .apiKey("apiKey1")
            .statusCode("statusCode1");
    }

    public static Agent getAgentSample2() {
        return new Agent()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .name("name2")
            .apiUrl("apiUrl2")
            .apiKey("apiKey2")
            .statusCode("statusCode2");
    }

    public static Agent getAgentRandomSampleGenerator() {
        return new Agent()
            .id(UUID.randomUUID())
            .name(UUID.randomUUID().toString())
            .apiUrl(UUID.randomUUID().toString())
            .apiKey(UUID.randomUUID().toString())
            .statusCode(UUID.randomUUID().toString());
    }
}
