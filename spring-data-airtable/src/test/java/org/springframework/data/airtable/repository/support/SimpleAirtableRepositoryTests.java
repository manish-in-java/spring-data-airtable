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

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link SimpleAirtableRepository}.
 */
public class SimpleAirtableRepositoryTests {
    /**
     * Tests that a repository cannot be constructed without an Airtable
     * client as it would be unable to persist the managed entities.
     */
    @Test
    public void testConstructWithoutAirtableOperations() {
        final var context = new AirtableMappingContext();

        final var entity = context.getPersistentEntity(Gamma.class);

        assertThrows(IllegalArgumentException.class
            , () -> new SimpleAirtableRepository<>(new AirtableEntityInformation<>(entity), null));
    }

    /**
     * Tests that a repository cannot be constructed without providing metadata
     * for the entities it manages.
     */
    @Test
    public void testConstructWithoutEntityInformation() {
        assertThrows(IllegalArgumentException.class
            , () -> new SimpleAirtableRepository<>(null, null));
    }
}
