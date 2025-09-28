package org.springframework.boot.airtable.autoconfigure;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.airtable.core.AirtableSettings;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Integration tests for {@link AirtableAutoConfiguration}.
 */
@SpringBootTest
public class AirtableAutoConfigurationTests {
    @Autowired
    private AirtableSettings connection;

    @Autowired
    private AirtableAutoConfiguration subject;

    /**
     * Tests that Airtable is configured automatically if the relevant details
     * are included in the Spring Boot application configuration file.
     */
    @Test
    public void autoConfigured() {
        assertNotNull(subject);
    }

    /**
     * Tests that Airtable settings are configured automatically if the relevant
     * details are included in the Spring Boot application configuration file.
     */
    @Test
    public void settingsAvailable() {
        assertNotNull(connection);

        System.out.println(connection.getBaseId());
        System.out.println(connection.getAccessToken());
    }
}
