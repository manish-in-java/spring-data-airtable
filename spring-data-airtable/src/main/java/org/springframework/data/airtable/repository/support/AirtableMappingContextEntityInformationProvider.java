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

import org.springframework.data.airtable.mapping.AirtablePersistentEntity;
import org.springframework.data.airtable.mapping.AirtablePersistentProperty;
import org.springframework.data.airtable.repository.AirtableEntityInformationProvider;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.util.Assert;

import static org.springframework.util.Assert.notNull;

/**
 * Provides metadata about a domain entity persisted to an Airtable table using
 * a {@link MappingContext}.
 */
final class AirtableMappingContextEntityInformationProvider implements AirtableEntityInformationProvider {
    private final MappingContext<? extends AirtablePersistentEntity<?>, AirtablePersistentProperty> context;

    /**
     * Sets the {@link MappingContext} to use for looking up domain entity
     * metadata.
     *
     * @param context A {@link MappingContext}.
     */
    AirtableMappingContextEntityInformationProvider(final MappingContext<? extends AirtablePersistentEntity<?>, AirtablePersistentProperty> context) {
        notNull(context, () -> "MappingContext must not be null!");

        this.context = context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> AirtableEntityInformation<T> getEntityInformation(final Class<T> domainClass) {
        final var persistentEntity = context.getPersistentEntity(domainClass);

        return (AirtableEntityInformation<T>) new AirtableEntityInformation<>(persistentEntity);
    }
}
