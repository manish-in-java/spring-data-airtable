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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

/**
 * Response from the Airtable List Records API.
 */
public class ListRecordsResponse {
    @JsonProperty
    private String offset;

    @JsonProperty
    private List<Record> records;

    /**
     * Gets an optional offset to use for fetching the next page of records
     * from the API - received only if the next page is available.
     *
     * @return An optional offset to use for fetching the next page of records.
     */
    public String getOffset() {
        return offset;
    }

    /**
     * Gets records received from Airtable.
     *
     * @return Records received from Airtable.
     */
    public List<Record> getRecords() {
        return records != null ? Collections.unmodifiableList(records) : Collections.emptyList();
    }
}
