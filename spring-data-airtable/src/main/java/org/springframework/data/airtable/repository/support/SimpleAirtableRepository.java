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

import org.springframework.data.airtable.core.AirtableOperations;
import org.springframework.data.airtable.repository.AirtableRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static org.springframework.util.Assert.notNull;

/**
 * Provides
 * <a href="https://martinfowler.com/eaaCatalog/repository.html">Repository</a>-style
 * access to entities stored in an Airtable table.
 *
 * @param <T> The type of entities.
 */
public class SimpleAirtableRepository<T> implements AirtableRepository<T> {
    private final AirtableEntityInformation<T> entityInformation;

    private final AirtableOperations operations;

    /**
     * Creates a repository for an entity type using metadata for the type and
     * an {@link AirtableOperations} to use for interacting with the Airtable
     * Web APIs.
     *
     * @param entityInformation Metadata about the entity type for this
     * repository.
     * @param operations The {@link AirtableOperations} to use for persisting
     * entity instances to Airtable.
     *
     * @throws IllegalArgumentException if {@code entityInformation} or
     * {@code operations} is
     * {@literal null}.
     */
    SimpleAirtableRepository(final AirtableEntityInformation<T> entityInformation
        , final AirtableOperations operations) {
        notNull(entityInformation, "AirtableEntityInformation must not be null.");
        notNull(operations, "AirtableOperations must not be null.");

        this.entityInformation = entityInformation;
        this.operations = operations;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<T> findAll(final Pageable page) {
        return operations.list(entityInformation, page);
    }
}
