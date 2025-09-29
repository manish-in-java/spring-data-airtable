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

package org.springframework.data.airtable.repository.support;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.airtable.domain.Gamma;
import org.springframework.data.airtable.mapping.AirtableMappingContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link AirtableMappingContextEntityInformationProvider}.
 */
public class AirtableMappingContextEntityInformationProviderTests {
    private AirtableMappingContextEntityInformationProvider subject;

    /**
     * Sets up objects required to run the tests.
     */
    @BeforeEach
    public void setup() {
        final var context = new AirtableMappingContext();

        subject = new AirtableMappingContextEntityInformationProvider(context);
    }

    /**
     * Tests that Airtable metadata cannot be determined without a mapping
     * context.
     */
    @Test
    public void testConstructWithoutContext() {
        assertThrows(IllegalArgumentException.class
            , () -> new AirtableMappingContextEntityInformationProvider(null));
    }

    /**
     * Tests that Airtable metadata can be determined for a domain entity from
     * its Java type.
     */
    @Test
    public void testGetEntityInformation() {
        assertNotNull(subject.getEntityInformation(Gamma.class));
    }

    /**
     * Tests that Airtable metadata cannot be determined without specifying the
     * Java type for the domain entity.
     */
    @Test
    public void testGetEntityInformationWithoutDomainClass() {
        assertThrows(IllegalArgumentException.class
            , () -> assertNotNull(subject.getEntityInformation(null)));
    }
}
