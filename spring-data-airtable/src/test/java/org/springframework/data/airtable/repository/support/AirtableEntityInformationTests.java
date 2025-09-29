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

import org.junit.jupiter.api.Test;
import org.springframework.data.airtable.domain.Gamma;
import org.springframework.data.airtable.mapping.AirtableMappingContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for {@link AirtableEntityInformation}.
 */
public class AirtableEntityInformationTests {
    /**
     * Tests that the name of the Airtable table to which entities are persisted
     * can be determined from the metadata for the entity type provided by
     * annotations.
     */
    @Test
    public void testGetTableName() {
        final var context = new AirtableMappingContext();

        final var entity = context.getPersistentEntity(Gamma.class);

        final var subject = new AirtableEntityInformation<>(entity);

        assertNotNull(subject.getTableName());
    }
}
