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
import org.springframework.data.annotation.Id;
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
    private AirtablePersistentEntity<Bar> entity;

    private AirtablePersistentProperty property;

    /**
     * Sets up objects required to run the tests.
     */
    @BeforeEach
    public void setup() throws NoSuchFieldException {
        final var holder = new SimpleTypeHolder(Set.of(Bar.class), true);

        final var subject = new AirtableMappingContext();

        entity = subject.createPersistentEntity(TypeInformation.of(Bar.class));

        property = subject.createPersistentProperty(Property.of(entity.getTypeInformation(), Bar.class.getDeclaredField("baz"))
            , entity
            , holder);
    }

    /**
     * Tests that metadata about a persistent entity can be generated correctly
     * from its type.
     */
    @Test
    public void testCreatePersistentEntity() {
        assertNotNull(entity);
        assertNotNull(entity.getType());
        assertEquals(Bar.class, entity.getType());
    }

    /**
     * Tests that metadata about a persistent property can be generated
     * correctly from its type.
     */
    @Test
    public void testCreatePersistentProperty() {
        assertNotNull(property);
    }
}

/**
 * A domain entity.
 */
@Table
class Bar {
    @Column
    private String baz;

    @Id
    private String id;
}
