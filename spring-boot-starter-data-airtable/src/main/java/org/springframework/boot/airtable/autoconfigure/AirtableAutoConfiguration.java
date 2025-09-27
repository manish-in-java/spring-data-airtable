package org.springframework.boot.airtable.autoconfigure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.airtable.core.Connection;

/**
 * Automatically configures Airtable if the relevant settings are included in
 * the Spring Boot application configuration file {@code application.properties}
 * or {@code application.yml}.
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
     * Creates an Airtable connection using settings available from the
     * application configuration file.
     *
     * @param baseId The unique identifier of the Airtable base to connect to.
     * @param accessToken The access token to use for invoking the Airtable
     * APIs to access the base.
     */
    @Bean
    Connection connection(
        @Value("${spring.airtable.base-id}") final String baseId
        , @Value("${spring.airtable.access-token}") final String accessToken
                         ) {
        return new Connection(baseId, accessToken);
    }
}
