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

package org.springframework.data.airtable.mapping;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.airtable.domain.Gamma;
import org.springframework.data.mapping.model.Property;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.util.TypeInformation;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for {@link AirtableMappingContext}.
 */
public class AirtableMappingContextTests {
    private SimpleTypeHolder holder;

    private AirtableMappingContext subject;

    /**
     * Sets up objects required to run the tests.
     */
    @BeforeEach
    public void setup() {
        holder = new SimpleTypeHolder(Set.of(Gamma.class), true);

        subject = new AirtableMappingContext();
    }

    /**
     * Tests that metadata about a persistent entity can be generated correctly
     * from its type.
     */
    @Test
    public void testCreatePersistentEntity() {
        final var entity = subject.createPersistentEntity(TypeInformation.of(Gamma.class));

        assertNotNull(entity);
        assertNotNull(entity.getType());
        assertEquals(Gamma.class, entity.getType());
    }

    /**
     * Tests that metadata about a persistent property can be generated
     * correctly from its type.
     */
    @Test
    public void testCreatePersistentProperty() throws NoSuchFieldException {
        final var entity = subject.createPersistentEntity(TypeInformation.of(Gamma.class));

        final var property = subject.createPersistentProperty(Property.of(entity.getTypeInformation(), Gamma.class.getDeclaredField("baz"))
            , entity
            , holder);

        assertNotNull(property);
    }
}
