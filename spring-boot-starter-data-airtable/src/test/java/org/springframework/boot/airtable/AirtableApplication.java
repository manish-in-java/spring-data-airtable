package org.springframework.boot.airtable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot Airtable Starter application.
 */
@SpringBootApplication
class AirtableApplication {
    /**
     * Main entry point for the application.
     *
     * @param args Command-line arguments passed to the program.
     */
    public static void main(final String[] args) {
        SpringApplication.run(AirtableApplication.class, args);
    }
}
