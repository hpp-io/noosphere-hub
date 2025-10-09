package io.hpp.noosphere.hub.config;

import com.google.common.primitives.Ints;
import io.swagger.v3.oas.models.tags.Tag;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import tech.jhipster.config.JHipsterConstants;
import tech.jhipster.config.JHipsterProperties;
import tech.jhipster.config.apidoc.customizer.JHipsterOpenApiCustomizer;

@Configuration
@Profile(JHipsterConstants.SPRING_PROFILE_API_DOCS)
public class OpenApiConfiguration {

    public static final String TAG_ORDER = "x-tag-order";
    public static final String API_FIRST_PACKAGE = "io.hpp.noosphere.hub.web.api";

    private static int tagOrder(Tag tag) {
        return Optional.ofNullable(tag.getExtensions())
            .map(map -> map.get(TAG_ORDER))
            .map(Objects::toString)
            .map(Ints::tryParse)
            .orElse(Integer.MAX_VALUE);
    }

    @Bean
    @ConditionalOnMissingBean(name = "apiFirstGroupedOpenAPI")
    public GroupedOpenApi apiFirstGroupedOpenAPI(
        JHipsterOpenApiCustomizer jhipsterOpenApiCustomizer,
        JHipsterProperties jHipsterProperties
    ) {
        JHipsterProperties.ApiDocs properties = jHipsterProperties.getApiDocs();
        return GroupedOpenApi.builder()
            .group("openapi")
            .addOpenApiCustomizer(jhipsterOpenApiCustomizer)
            .packagesToScan(API_FIRST_PACKAGE)
            .pathsToMatch(properties.getDefaultIncludePattern())
            .build();
    }

    @Bean
    public OpenApiCustomizer sortTags() {
        Comparator<Tag> cmp = Comparator.comparingInt(OpenApiConfiguration::tagOrder).thenComparing(Tag::getName);

        return openApi -> openApi.getTags().sort(cmp);
    }
}
