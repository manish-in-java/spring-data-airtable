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

package org.springframework.data.airtable.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

/**
 * Airtable-specific extension of
 * {@link org.springframework.data.repository.CrudRepository} that allows working
 * with an Airtable table.
 *
 * @see <a href="https://www.airtable.com" title="Airtable">Airtable</a>
 */
@NoRepositoryBean
public interface AirtableRepository<T> extends Repository<T, String> {
    /**
     * Finds a page of records in a table. Airtable returns one page of records
     * at a time. Each page can contain a specified number of records, which
     * must be equal to or less than 100.
     *
     * @param pageable The page of records to find.
     *
     * @return The requested page, if available.
     *
     * @see <a href="https://airtable.com/developers/web/api/list-records" title="Airtable - List records">Airtable - List records</a>
     */
    Page<T> findAll(Pageable pageable);
}
