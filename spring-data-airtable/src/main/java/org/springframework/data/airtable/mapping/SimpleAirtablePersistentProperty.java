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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Column getColumn() {
        return getField().getAnnotation(Column.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Association<AirtablePersistentProperty> createAssociation() {
        return null;
    }
}
