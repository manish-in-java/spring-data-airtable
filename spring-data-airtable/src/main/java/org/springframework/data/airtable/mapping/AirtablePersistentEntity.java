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

import org.springframework.data.mapping.model.MutablePersistentEntity;

/**
 * Contract for a domain entity whose instances can be persisted to an Airtable
 * table as a data stream.
 *
 * @param <T> The actual type of the domain entity.
 */
public interface AirtablePersistentEntity<T>
    extends MutablePersistentEntity<T, AirtablePersistentProperty> {
    /**
     * Gets the name of the Airtable table to which instances of this entity
     * should be persisted. The table name is required for invoking the Airtable
     * Web APIs. For higher security, the table identifier can also be used in
     * place of the actual name of the table.
     *
     * @return The name of the Airtable table to which instances of this entity
     * should be persisted.
     */
    String getTableName();
}
