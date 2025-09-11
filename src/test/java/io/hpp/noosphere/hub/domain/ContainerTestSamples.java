package io.hpp.noosphere.hub.domain;

import java.util.UUID;

public class ContainerTestSamples {

    public static Container getContainerSample1() {
        return new Container()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .name("name1")
            .walletAddress("walletAddress1")
            .statusCode("statusCode1");
    }

    public static Container getContainerSample2() {
        return new Container()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .name("name2")
            .walletAddress("walletAddress2")
            .statusCode("statusCode2");
    }

    public static Container getContainerRandomSampleGenerator() {
        return new Container()
            .id(UUID.randomUUID())
            .name(UUID.randomUUID().toString())
            .walletAddress(UUID.randomUUID().toString())
            .statusCode(UUID.randomUUID().toString());
    }
}
