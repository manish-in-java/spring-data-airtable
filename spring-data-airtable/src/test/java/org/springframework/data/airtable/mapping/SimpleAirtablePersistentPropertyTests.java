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
import org.springframework.data.airtable.domain.Beta;
import org.springframework.data.mapping.model.Property;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.util.TypeInformation;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SimpleAirtablePersistentProperty}.
 */
public class SimpleAirtablePersistentPropertyTests {
    private AirtablePersistentEntity<Beta> entity;

    private SimpleTypeHolder holder;

    private Property property;

    /**
     * Sets up objects required to run the tests.
     */
    @BeforeEach
    public void setup() throws NoSuchFieldException {
        entity = new SimpleAirtablePersistentEntity<>(TypeInformation.of(Beta.class));

        holder = new SimpleTypeHolder(Set.of(Beta.class), true);

        property = Property.of(entity.getTypeInformation(), Beta.class.getDeclaredField("bar"));
    }

    /**
     * Tests that a property cannot be used as an association to another entity.
     */
    @Test
    public void testCreateAssociation() {
        assertNull(getProperty().createAssociation());
    }

    /**
     * Tests that field metadata can be determined for a property.
     */
    @Test
    public void testGetFieldName() {
        assertNotNull(getProperty().getFieldName());
    }

    /**
     * Tests that a property is not treated as an association to another entity.
     */
    @Test
    public void testIsAssociation() {
        assertFalse(getProperty().isAssociation());
    }

    /**
     * Creates metadata for an annotated field.
     */
    private SimpleAirtablePersistentProperty getProperty() {
        return new SimpleAirtablePersistentProperty(property, entity, holder);
    }
}
