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

import org.springframework.data.mapping.model.BasicPersistentEntity;
import org.springframework.data.util.TypeInformation;

import java.util.Locale;

/**
 * Reads metadata about an entity persisted to Airtable.
 *
 * @param <T> The type of entity to persist to Airtable.
 */
class SimpleAirtablePersistentEntity<T>
    extends BasicPersistentEntity<T, AirtablePersistentProperty>
    implements AirtablePersistentEntity<T> {
    private final String tableName;

    /**
     * Creates a new instance.
     *
     * @param typeInformation A {@link TypeInformation} containing metadata
     * about the actual domain entity to be persisted.
     */
    public SimpleAirtablePersistentEntity(final TypeInformation<T> typeInformation) {
        super(typeInformation);

        this.tableName = extractTableName(typeInformation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTableName() {
        return tableName;
    }

    /**
     * <p>
     * Derives the name of the Airtable table to which the entity instances
     * must be persisted.
     * </p>
     * <ul>
     * <li>First, the {@link Table} annotation on the entity class is checked
     * to see if {@link Table#name()} has been specified. If yes, the specified
     * name is used.</li>
     * <li>If the name has not been specified, the simple name of the entity
     * class is used. For example, the data stream name for an entity class
     * named {@code Person} will be considered to be {@code person}
     * (all lowercase).</li>
     * </ul>
     *
     * @param typeInformation Metadata about the entity class for which the
     * table name is required.
     *
     * @return The name of the Airtable table to which the entity instances
     * must be persisted.
     */
    private String extractTableName(final TypeInformation<T> typeInformation) {
        final var table = findAnnotation(Table.class);

        // Check if the entity class has the @Table annotation and that
        // the table name has been specified through the annotation.
        if (table == null) {
            return null;
        }

        return !table.name().isBlank()
               // Return the stream name specified through the annotation.
               ? table.name().trim()
               // Otherwise, return the lowercase version of the entity class's
               // simple name.
               : typeInformation.getType().getSimpleName().toLowerCase(Locale.ENGLISH);
    }
}
