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

package org.springframework.data.airtable.domain;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Date;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableMap;

/**
 * <p>
 * A record in an Airtable table, which is of the following form.
 * </p>
 *
 * <pre>{@code
 * {
 *      "id": "rec3lbPRG4aVqkeOQ",
 *      "createdTime": "2022-09-12T21:03:48.000Z",
 *      "fields": {
 *          "Address": "1 Ferry Building",
 *          "Name": "Ferry Building"
 *      }
 * }
 * }</pre>
 *
 * <p>
 * The table columns are returned in the {@code fields} map.
 * </p>
 */
public class Record {
    private Date createdTime;

    private Map<String, JsonNode> fields;

    private String id;

    /**
     * Gets the date and time at which the record was created.
     *
     * @return The date and time at which the record was created.
     */
    public Date getCreatedTime() {
        return createdTime;
    }

    /**
     * Gets the record fields.
     *
     * @return The record fields.
     */
    public Map<String, JsonNode> getFields() {
        return fields == null ? emptyMap() : unmodifiableMap(fields);
    }

    /**
     * Gets the unique record identifier.
     *
     * @return The unique record identifier.
     */
    public String getId() {
        return id;
    }
}
