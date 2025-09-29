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

import org.junit.jupiter.api.Test;
import org.springframework.data.util.TypeInformation;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SimpleAirtablePersistentEntity}.
 */
public class SimpleAirtablePersistentEntityTests {
    /**
     * Tests that entity metadata cannot be determined without knowing the
     * entity type.
     */
    @Test
    public void testConstructWithoutTypeInformation() {
        assertThrows(IllegalArgumentException.class
            , () -> new SimpleAirtablePersistentEntity<>(null)
                    );
    }

    /**
     * Tests that Airtable metadata cannot be determined for a class not
     * annotated with the required annotation.
     */
    @Test
    public void testGetTableWithoutAnnotation() {
        assertNull(new SimpleAirtablePersistentEntity<>(TypeInformation.of(Alpha.class)).getTableName());
    }

    /**
     * Tests that the name of the Airtable table to which entities of a certain
     * type are persisted can be determined from the annotation added to the
     * entity class.
     */
    @Test
    public void testGetTableWithoutTableName() {
        final var table = new SimpleAirtablePersistentEntity<>(TypeInformation.of(Beta.class)).getTableName();

        assertNotNull(table);
        assertNotNull(Beta.class.getSimpleName().toLowerCase(Locale.ENGLISH), table);
    }

    /**
     * Tests that the name of the Airtable table to which entities of a certain
     * type are persisted can be determined from the annotation added to the
     * entity class.
     */
    @Test
    public void testGetTableWithTableName() {
        final var table = new SimpleAirtablePersistentEntity<>(TypeInformation.of(Gamma.class)).getTableName();

        assertNotNull(table);
        assertNotNull(Gamma.class.getSimpleName(), table);
    }
}

/**
 * A domain entity not mapped to any Airtable table.
 */
class Alpha {
}

/**
 * A domain entity mapped to an Airtable table with name determined implicitly.
 */
@Table
class Beta {
}

/**
 * A domain entity mapped to an Airtable table with name defined explicitly.
 */
@Table(name = "Gamma")
class Gamma {
}
