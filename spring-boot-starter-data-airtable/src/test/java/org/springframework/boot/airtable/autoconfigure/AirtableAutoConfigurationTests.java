package org.springframework.boot.airtable.autoconfigure;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.airtable.core.Connection;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Integration tests for {@link AirtableAutoConfiguration}.
 */
@SpringBootTest
public class AirtableAutoConfigurationTests {
    @Autowired
    private Connection connection;

    @Autowired
    private AirtableAutoConfiguration subject;

    /**
     * Tests that Airtable is configured automatically if the relevant
     * settings are included in the Spring Boot application configuration file.
     */
    @Test
    public void autoConfigured() {
        assertNotNull(subject);
    }

    /**
     * Tests that an Airtable connection automatically if the relevant
     * settings are included in the Spring Boot application configuration file.
     */
    @Test
    public void connectionAvailable() {
        assertNotNull(connection);

        System.out.println(connection.getBaseId());
        System.out.println(connection.getAccessToken());
    }
}
