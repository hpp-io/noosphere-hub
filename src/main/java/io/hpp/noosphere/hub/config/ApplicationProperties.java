package io.hpp.noosphere.hub.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Noo Sphere Hub.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@Data
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final Liquibase liquibase = new Liquibase();
    private final Keycloak keycloak = new Keycloak();

    // jhipster-needle-application-properties-property


    // jhipster-needle-application-properties-property-getter

    @Data
    public static class Liquibase {

        private Boolean asyncStart = true;

    }
    // jhipster-needle-application-properties-property-class

    @Data
    public static class Keycloak {

        private String authUrl;
        private String realmId;
        private String adminClientId;
        private String adminClientSecret;

    }

}
