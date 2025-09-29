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

import org.springframework.data.mapping.context.AbstractMappingContext;
import org.springframework.data.mapping.model.Property;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.util.TypeInformation;

/**
 * Generates metadata for persisting domain entities to Airtable tables using
 * metadata available form Java types.
 */
public class AirtableMappingContext
    extends AbstractMappingContext<AirtablePersistentEntity<?>, AirtablePersistentProperty> {
    /**
     * {@inheritDoc}
     */
    @Override
    protected <T> AirtablePersistentEntity<T> createPersistentEntity(final TypeInformation<T> typeInformation) {
        return new SimpleAirtablePersistentEntity<>(typeInformation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AirtablePersistentProperty createPersistentProperty(final Property property
        , final AirtablePersistentEntity<?> owner
        , final SimpleTypeHolder typeHolder) {
        return new SimpleAirtablePersistentProperty(property, owner, typeHolder);
    }
}
