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

package org.springframework.data.airtable.core;

/**
 * A connection for accessing an Airtable base (short for "database") using
 * Airtable APIs.
 */
public class Connection {
    private final String accessToken;

    private final String baseId;

    /**
     * Creates a connection for an Airtable base.
     *
     * @param baseId The unique identifier of the base to access - must not be
     * blank.
     * @param accessToken The access token for invoking the Airtable APIs - must
     * not be blank.
     */
    public Connection(final String baseId, final String accessToken) {
        this.accessToken = accessToken;
        this.baseId = baseId;
    }

    /**
     * Gets the access token to use for invoking the Airtable APIs - usually a
     * random string. The token must be authorized for the required access
     * (read and/or write) to the base. Invoking an operation for which the
     * token does not have the required access on the base will result in access
     * denied errors from Airtable. Access level for a token can be changed
     * anytime from the Airtable web console. This means an operation that is
     * denied by Airtable can be made to work by adding the required access to
     * the token from the Airtable web console. Conversely, an operation that
     * previously worked may stop working if the required access is revoked
     * from the token.
     *
     * @return The access token to use for invoking the Airtable APIs.
     *
     * @see <a href="https://airtable.com/create/tokens">Airtable Personal Access Tokens</a>
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Gets the unique identifier of the Airtable base to access. See the
     * automatically-generated API documentation page for the base to get the
     * unique base identifier.
     *
     * @return The unique identifier of the Airtable base.
     */
    public String getBaseId() {
        return baseId;
    }
}
