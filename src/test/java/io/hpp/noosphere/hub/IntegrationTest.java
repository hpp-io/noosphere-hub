package io.hpp.noosphere.hub;

import io.hpp.noosphere.hub.config.AsyncSyncConfiguration;
import io.hpp.noosphere.hub.config.EmbeddedSQL;
import io.hpp.noosphere.hub.config.JacksonConfiguration;
import io.hpp.noosphere.hub.config.TestSecurityConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = { NoosphereHubApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class, TestSecurityConfiguration.class }
)
@EmbeddedSQL
public @interface IntegrationTest {
}
