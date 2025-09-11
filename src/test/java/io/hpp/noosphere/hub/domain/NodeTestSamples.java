package io.hpp.noosphere.hub.domain;

import java.util.UUID;

public class NodeTestSamples {

    public static Node getNodeSample1() {
        return new Node()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .name("name1")
            .apiUrl("apiUrl1")
            .apiKey("apiKey1")
            .statusCode("statusCode1");
    }

    public static Node getNodeSample2() {
        return new Node()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .name("name2")
            .apiUrl("apiUrl2")
            .apiKey("apiKey2")
            .statusCode("statusCode2");
    }

    public static Node getNodeRandomSampleGenerator() {
        return new Node()
            .id(UUID.randomUUID())
            .name(UUID.randomUUID().toString())
            .apiUrl(UUID.randomUUID().toString())
            .apiKey(UUID.randomUUID().toString())
            .statusCode(UUID.randomUUID().toString());
    }
}
