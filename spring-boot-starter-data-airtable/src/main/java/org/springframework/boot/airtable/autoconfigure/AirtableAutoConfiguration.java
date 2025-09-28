package org.springframework.boot.airtable.autoconfigure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.airtable.core.AirtableSettings;

/**
 * <p>
 * Automatically configures Airtable if the relevant properties are included in
 * the Spring Boot application configuration file {@code application.properties}
 * or {@code application.yml}.
 * </p>
 *
 * <pre>{@code
 * application.properties
 * -----------------------------------------------------------------------------
 * spring.airtable.base-id=
 * spring.airtable.access-token=
 * }</pre>
 *
 * <pre>{@code
 * application.yml
 * -----------------------------------------------------------------------------
 * spring:
 *      airtable:
 *          base-id:
 *          access-token:
 * }</pre>
 */
@ConditionalOnProperty(
    prefix = "spring.airtable",
    name = "access-token"
)
@ConditionalOnProperty(
    prefix = "spring.airtable",
    name = "base-id"
)
@Configuration(
    proxyBeanMethods = false
)
class AirtableAutoConfiguration {
    /**
     * Creates Airtable settings using properties available from the application
     * configuration file.
     *
     * @param baseId The unique identifier of the Airtable base to connect to.
     * @param accessToken The access token to use for invoking the Airtable
     * APIs to access the base.
     */
    @Bean
    AirtableSettings settings(@Value("${spring.airtable.base-id}") final String baseId
        , @Value("${spring.airtable.access-token}") final String accessToken) {
        return new AirtableSettings(baseId, accessToken);
    }
}
