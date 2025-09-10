package ai.hpp.noosphere.hub;

import ai.hpp.noosphere.hub.config.AsyncSyncConfiguration;
import ai.hpp.noosphere.hub.config.EmbeddedPulsar;
import ai.hpp.noosphere.hub.config.EmbeddedSQL;
import ai.hpp.noosphere.hub.config.JacksonConfiguration;
import ai.hpp.noosphere.hub.config.TestSecurityConfiguration;
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
    classes = { NooSphereHubApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class, TestSecurityConfiguration.class }
)
@EmbeddedSQL
@EmbeddedPulsar
public @interface IntegrationTest {
}
