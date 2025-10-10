package org.springframework.boot.airtable.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.airtable.core.AirtableOperations;
import org.springframework.data.airtable.core.AirtableSettings;
import org.springframework.data.airtable.core.AirtableTemplate;

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
public class AirtableAutoConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(AirtableAutoConfiguration.class);

    /**
     * Creates an Airtable client using properties available from the
     * application configuration file.
     *
     * @param baseId The unique identifier of the Airtable base to connect to.
     * @param accessToken The access token to use for invoking the Airtable
     * APIs to access the base.
     *
     * @return An {@link AirtableOperations} instance.
     */
    @Bean
    AirtableOperations airtableOperations(@Value("${spring.airtable.base-id}") final String baseId
        , @Value("${spring.airtable.access-token}") final String accessToken) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creating Airtable settings with base id {} read from "
                             + "Spring Boot configuration.", baseId);
        }

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Using Airtable access token {} read from Spring Boot "
                             + "configuration.", accessToken);
        }

        final var settings = new AirtableSettings(baseId, accessToken);

        return new AirtableTemplate(settings);
    }
}
