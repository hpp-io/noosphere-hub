package io.hpp.noosphere.hub.domain;

import java.util.UUID;

public class NodeContainerTestSamples {

    public static NodeContainer getNodeContainerSample1() {
        return new NodeContainer().id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa")).statusCode("statusCode1");
    }

    public static NodeContainer getNodeContainerSample2() {
        return new NodeContainer().id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367")).statusCode("statusCode2");
    }

    public static NodeContainer getNodeContainerRandomSampleGenerator() {
        return new NodeContainer().id(UUID.randomUUID()).statusCode(UUID.randomUUID().toString());
    }
}
