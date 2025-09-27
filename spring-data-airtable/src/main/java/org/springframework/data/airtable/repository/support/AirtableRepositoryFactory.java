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
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

/**
 * Factory for Airtable repositories.
 */
final class AirtableRepositoryFactory extends RepositoryFactorySupport {
    private final AirtableOperations operations;

    /**
     * Creates a factory that can create Airtable repositories.
     *
     * @param operations Core operations wrapped by the repositories to create.
     */
    AirtableRepositoryFactory(final AirtableOperations operations) {
        this.operations = operations;
    }

    @Override
    public <T, ID> EntityInformation<T, ID> getEntityInformation(final Class<T> domainClass) {
        return null;
    }
    @Override
    protected Object getTargetRepository(final RepositoryInformation metadata) {
        return null;
    }
    @Override
    protected Class<?> getRepositoryBaseClass(final RepositoryMetadata metadata) {
        return null;
    }
}
