/*
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.data.airtable.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.airtable.autoconfigure.AirtableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.airtable.repository.config.EnableAirtableRepositories;

/**
 * Spring Data Airtable example application.
 */
@EnableAirtableRepositories
@Import(AirtableAutoConfiguration.class)
@SpringBootApplication
public class AirtableApplication {
    /**
     * Main entry point for the application.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(final String[] args) {
        SpringApplication.run(AirtableApplication.class, args);
    }
}
