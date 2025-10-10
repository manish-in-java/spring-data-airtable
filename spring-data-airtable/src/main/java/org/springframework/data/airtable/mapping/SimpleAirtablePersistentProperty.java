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

import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.model.AnnotationBasedPersistentProperty;
import org.springframework.data.mapping.model.Property;
import org.springframework.data.mapping.model.SimpleTypeHolder;

/**
 * Reads metadata about Java properties to be persisted to Airtable.
 */
class SimpleAirtablePersistentProperty
    extends AnnotationBasedPersistentProperty<AirtablePersistentProperty>
    implements AirtablePersistentProperty {
    private final String fieldName;

    /**
     * Creates metadata for a persistent property.
     *
     * @param property The persistent property.
     * @param owner The entity type for the property.
     * @param simpleTypeHolder Metadata for the entity type.
     *
     * @throws IllegalArgumentException if {@code owner} or
     * {@code simpleTypeHolder} is
     * {@literal null}.
     */
    public SimpleAirtablePersistentProperty(final Property property
        , final PersistentEntity<?, AirtablePersistentProperty> owner
        , final SimpleTypeHolder simpleTypeHolder) {
        super(property, owner, simpleTypeHolder);

        this.fieldName = extractFieldName(property);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFieldName() {
        return fieldName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Association<AirtablePersistentProperty> createAssociation() {
        return null;
    }

    /**
     * <p>
     * Derives the name of the Airtable table field to which the property
     * must be persisted.
     * </p>
     *
     * <ul>
     *      <li>First, the {@link Field} annotation on the property is checked
     *      to see if {@link Field#name()} has been specified. If yes, the
     *      specified name is used.</li>
     *      <li>If the name has not been specified, the simple name of the
     *      property is used. For example, the field name for an entity field
     *      named {@code name} will be considered to be {@code name}
     *      (all lowercase).</li>
     * </ul>
     *
     * @param property Metadata about the property for which the field name is
     * required.
     *
     * @return The name of the Airtable field to which the property must be
     * persisted.
     */
    private String extractFieldName(final Property property) {
        final var field = getField().getAnnotation(Field.class);

        if (field == null) {
            return null;
        }

        return !field.name().isBlank()
               // Return the field specified through the annotation.
               ? field.name().trim()
               // Otherwise, return the property name.
               : property.getName();
    }
}
