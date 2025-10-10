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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.airtable.domain.ListRecordsResponse;
import org.springframework.data.airtable.mapping.*;
import org.springframework.data.airtable.repository.support.AirtableEntityInformation;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.mapping.PreferredConstructor;
import org.springframework.data.mapping.model.PreferredConstructorDiscoverer;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.lang.String.format;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Accesses data in an Airtable table using the Airtable Web API.
 */
public final class AirtableTemplate implements AirtableOperations {
    private static final String AIRTABLE_API_URL = "https://api.airtable.com/v0";

    private static final ConcurrentMap<AirtablePersistentEntity<?>, AirtableEntityMappingData> ENTITY_FIELD_MAPPINGS = new ConcurrentHashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(AirtableTemplate.class);

    private static final AirtableMappingContext MAPPING_CONTEXT = new AirtableMappingContext();

    private static final ConcurrentMap<String, String> RECORD_OFFSETS = new ConcurrentHashMap<>();

    private final AirtableSettings settings;

    /**
     * Creates a template using specified settings for Airtable Web APIs.
     *
     * @param settings Settings to use for invoking Airtable Web APIs.
     */
    public AirtableTemplate(final AirtableSettings settings) {
        this.settings = settings;
    }

    /**
     * Lists records of a given type one page at a time.
     *
     * @param entity The type of records to list - must be annotated with the
     * {@link Table} annotation that specifies the name of the table from which
     * the records should be fetched.
     * @param page The page to retrieve.
     * @param <T> The type of records.
     *
     * @return A page of records of the given type.
     *
     * @throws MappingException if the specified type is not correctly mapped
     * as an Airtable persistent entity - this is normally done by annotating
     * the entity class with {@link Table}.
     * @throws IllegalStateException if the specified type does not have any
     * field annotated with {@link Id} or if the type does not contain any
     * field annotated with {@link Field}.
     * @see <a href="https://airtable.com/developers/web/api/list-records">Airtable List Records API</a>
     */
    @Override
    public <T> Page<T> list(final AirtableEntityInformation<T> entity, final Pageable page) {
        // Determine the type of entities to return.
        final var type = MAPPING_CONTEXT.getPersistentEntity(entity.getJavaType());

        if (type == null) {
            throw new MappingException("Unmapped type " + entity.getJavaType().getName() + ".");
        }

        String offset = "";
        if (page.getPageNumber() != 0) {
            // The request is not for the first page. The Airtable List Records
            // API does not accept arbitrary page numbers. Instead, where
            // pagination is possible, the API returns an offset in the response
            // to fetch the next page.
            //
            // We need to check if the previous page was fetched earlier and
            // if the offset for the requested page is available.
            final var key = getPageOffsetKey(type, page.getPageNumber());

            if (!RECORD_OFFSETS.containsKey(key)) {
                throw new IllegalStateException("Page number "
                                                    + page.getPageNumber()
                                                    + " cannot be fetched for"
                                                    + " type"
                                                    + entity.getJavaType().getName()
                                                    + " because the Airtable "
                                                    + " record offset for that"
                                                    + " page number is"
                                                    + " unavailable.");
            }

            offset = RECORD_OFFSETS.get(key);
        }

        // Determine the mapping metadata for the entity.
        final var metadata = getEntityMappingData(type);
        final var id = metadata.getIdProperty();
        final var mappings = metadata.getFieldMappings();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Invoking Airtable List Records API for table name {}."
                , entity.getTableName());
        }

        // Invoke the Airtable List Records API.
        final var parameters = Map.of("pageSize", page.getPageSize()
            , "offset", offset);
        final var response = get(getEndpoint(entity.getTableName())
            , parameters
            , ListRecordsResponse.class).getBody();

        // Extract records from the response.
        final var records = response.getRecords();

        if (records.isEmpty()) {
            return new PageImpl<>(Collections.emptyList());
        }

        try {
            final List<T> entities = new ArrayList<>();

            for (final var record : records) {
                final Map<String, JsonNode> properties = new HashMap<>();

                final var fields = record.getFields();

                if (fields != null && !fields.isEmpty()) {
                    // Map Airtable field values to Java property values.
                    for (final var mapping : mappings.entrySet()) {
                        final var airtableFieldName = mapping.getKey();
                        final var entityProperty = mapping.getValue();

                        if (fields.containsKey(airtableFieldName)) {
                            properties.put(entityProperty.getField().getName(), fields.get(airtableFieldName));
                        }
                    }
                }

                // Create an entity instance.
                final T instance = new ObjectMapper().convertValue(properties, entity.getJavaType());

                // Add record identifier.
                id.getSetter().invoke(instance, record.getId());

                entities.add(instance);
            }

            if (response.getOffset() != null
                && !response.getOffset().isBlank()) {
                // Store the offset for fetching the next page of records.
                synchronized (RECORD_OFFSETS) {
                    RECORD_OFFSETS.put(getPageOffsetKey(type, page.getPageNumber() + 1), response.getOffset());
                }
            }

            return new PageImpl<>(entities);
        }
        catch (final IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Performs an HTTP DELETE request and returns the response received as a
     * result of the request.
     *
     * @param url The URL to invoke.
     * @param responseType The type of response expected from the URL if it
     * completes successfully.
     * @param <R> The type of response to return.
     *
     * @return Response from the URL.
     */
    private <R> ResponseEntity<R> delete(final String url
        , final Class<R> responseType) {
        return delete(url, null, responseType);
    }

    /**
     * Performs an HTTP DELETE request and returns the response received as a
     * result of the request.
     *
     * @param url The URL to invoke.
     * @param body Optional body to include with the request.
     * @param responseType The type of response expected from the URL if it
     * completes successfully.
     * @param <R> The type of response to return.
     *
     * @return Response from the URL.
     */
    private <Q, R> ResponseEntity<R> delete(final String url
        , final Q body
        , final Class<R> responseType) {
        return exchange(DELETE, url, body, responseType);
    }

    /**
     * Performs an HTTP GET request and returns the response received as a
     * result of the request.
     *
     * @param url The URL to invoke.
     * @param parameters Parameters to include in the request URL.
     * @param responseType The type of response expected from the URL if it
     * completes successfully.
     * @param <R> The type of response to return.
     *
     * @return Response from the URL.
     */
    private <R> ResponseEntity<R> get(final String url
        , final Map<String, ?> parameters
        , final Class<R> responseType) {
        final var urlBuilder = UriComponentsBuilder.fromHttpUrl(url);

        Optional.ofNullable(parameters)
                .orElseGet(Collections::emptyMap)
                .keySet()
                .forEach(param -> urlBuilder.queryParam(param, format("{%s}", param)));

        final var urlTemplate = urlBuilder.encode().toUriString();

        final var restTemplate = new RestTemplate();

        try {
            // Invoke the specified REST endpoint.
            return restTemplate.exchange(urlTemplate
                , GET
                , new HttpEntity<>(getHeaders())
                , responseType
                , parameters);
        }
        catch (final HttpStatusCodeException e) {
            LOGGER.error("Invocation of REST endpoint [{}] resulted in error "
                             + "code [{}] and the following exception."
                , url
                , e.getStatusCode()
                , e);

            // If an HTTP error is thrown upon invoking the endpoint, the REST
            // template throws an exception and does not parse the response
            // body. For this reason, we forcibly parse the response body so
            // that the body is returned in full to the caller. We use the
            // underlying infrastructure for parsing the response body to
            // take advantage of the entire built-in resolution of response
            // content type.
            try (final var content = new ByteArrayClientHttpResponse(e.getResponseBodyAsByteArray(), e.getResponseHeaders())) {
                final var response = new HttpMessageConverterExtractor<>(responseType
                    , restTemplate.getMessageConverters())
                    .extractData(content);

                return new ResponseEntity<>(response, e.getStatusCode());
            }
            catch (final IOException i) {
                throw new RuntimeException(i);
            }
        }
    }

    /**
     * Gets the API endpoint for an Airtable table having a given name or
     * identifier.
     *
     * @param table The name or identifier of the Airtable table for which the
     * API endpoint is required.
     *
     * @return API endpoint for the requested table.
     */
    private String getEndpoint(final String table) {
        return format("%s/%s/%s", AIRTABLE_API_URL, settings.getBaseId(), table);
    }

    /**
     * Gets mapping metadata for a persistent entity, which includes the
     * preferred constructor method to use for entity instances, identifier
     * property and field mappings.
     *
     * @param entity The entity type for which the metadata is required.
     *
     * @return The metadata for the entity.
     */
    private AirtableEntityMappingData getEntityMappingData(final AirtablePersistentEntity<?> entity) {
        if (!ENTITY_FIELD_MAPPINGS.containsKey(entity)) {
            synchronized (ENTITY_FIELD_MAPPINGS) {
                // Find preferred constructor method for the entity.
                final var preferredConstructor = PreferredConstructorDiscoverer.discover(entity);

                // Ensure that a constructor method is available.
                if (preferredConstructor == null) {
                    throw new MappingException("Unmanaged data type " + entity.getName() + ".");
                }

                // Determine the identifier property for the entity.
                final var id = entity.getRequiredIdProperty();

                // Ensure that the identifier property is available.
                if (id.getField() == null) {
                    throw new IllegalStateException("Type " + entity.getName()
                                                        + " does not have an"
                                                        + " identifier field"
                                                        + " annotated with @Id.");
                }

                // Ensure that the getter for the identifier property is
                // available.
                if (id.getGetter() == null) {
                    throw new MappingException("Field " + id.getField().getName()
                                                   + " on type " + entity.getName()
                                                   + " does not have a"
                                                   + " publicly-accessible"
                                                   + " getter method.");
                }

                // Ensure that the setter for the identifier property is
                // available.
                if (id.getSetter() == null) {
                    throw new MappingException("Field " + id.getField().getName()
                                                   + " on type " + entity.getName()
                                                   + " does not have a"
                                                   + " publicly-accessible"
                                                   + " setter method.");
                }

                // Determine property mappings for the entity.
                final var properties = getPersistentProperties(entity);

                if (properties == null || properties.isEmpty()) {
                    throw new IllegalStateException("Type " + entity.getName()
                                                        + " does not have any"
                                                        + " field annotated"
                                                        + " with @Field.");
                }

                for (final var property : properties.values()) {
                    // Ensure that the getter for the property is available.
                    if (property.getGetter() == null) {
                        throw new MappingException("Field " + property.getField().getName()
                                                       + " on type " + entity.getName()
                                                       + " does not have a"
                                                       + " publicly-accessible"
                                                       + " getter method.");
                    }

                    // Ensure that the setter for the property is available.
                    if (property.getSetter() == null) {
                        throw new MappingException("Field " + property.getField().getName()
                                                       + " on type " + entity.getName()
                                                       + " does not have a"
                                                       + " publicly-accessible"
                                                       + " setter method.");
                    }
                }

                ENTITY_FIELD_MAPPINGS.put(entity, new AirtableEntityMappingData(preferredConstructor, id, properties));
            }
        }

        return ENTITY_FIELD_MAPPINGS.get(entity);
    }

    /**
     * Gets the key to use as lookup for fetching a particular page of records
     * for a given entity.
     *
     * @param entity An entity.
     * @param page The page number to fetch.
     *
     * @return The lookup key to use for the page if available.
     */
    private String getPageOffsetKey(final AirtablePersistentEntity<?> entity, final int page) {
        return String.format("%s-%s-%d"
            , settings.getBaseId()
            , entity.getTableName()
            , page);
    }

    /**
     * <p>
     * Gets field mappings for an Airtable entity.
     * </p>
     *
     * <pre>{@code
     * For example, consider an Airtable table named {@code Pets} having fields
     * named "Pet Name", "Pet Type" and "Age in Years", respectively (without
     * quotes). Data from this table is returned by Airtable as follows:
     *
     * {
     *     "records": [{
     *         "id": "igsa31vhsj",
     *         "fields: {
     *             "Pet Name": "Alice",
     *             "Pet Type": "Cat",
     *             "Age in Years": 1
     *         }
     *     },
     *     {
     *         "id": "rir66h46e9",
     *         "fields: {
     *             "Pet Name": "Bob",
     *             "Pet Type": "Dog",
     *             "Age in Years": 2
     *          }]
     *     }]
     * }
     *
     * Also consider the following Java class that should be mapped to this
     * table:
     *
     * @Table(name = "Pets")
     * class Pet {
     *     @Field(name = "Pet Name")
     *     private String name;
     *
     *     @Field(name = "Pet Type")
     *     private PetType type;
     *
     *     @Field(name = "Age in Years")
     *     private int age;
     * }
     *
     * As evident, the class has properties named "name", "type" and "age"
     * (without quotes) that do not match the field names in the table. This
     * discrepancy is addressed by annotating each Java field with the
     * @Field annotation and specifying the name of the field in the Airtable
     * table.</pre>
     *
     * <p>
     * This method maps the Airtable field names to the correct Java properties
     * so that data can be copied from an Airtable API response to a Java
     * object. For the example Airtable table and Java class above, this method
     * will return the following mappings:
     * </p>
     *
     * <pre>{@code
     * {
     *     "Pet Name": "name",
     *     "Pet Type": "type",
     *     "Age in Years": "age"
     * }
     * }
     * </pre>
     *
     * @param entity The entity type for which field mappings are required.
     *
     * @return Field mappings for the entity type.
     */
    private <T> Map<String, AirtablePersistentProperty> getPersistentProperties(final AirtablePersistentEntity<T> entity) {
        // Find mapped properties for the entity type.
        final var properties = entity.getPersistentProperties(Field.class);

        if (!properties.iterator().hasNext()) {
            // No field mappings found for the entity.
            return null;
        }

        final Map<String, AirtablePersistentProperty> mappings = new HashMap<>();

        for (final var property : properties) {
            mappings.put(property.getFieldName(), property);
        }

        return mappings;
    }

    /**
     * Performs an HTTP POST request and returns the response received as a
     * result of the request.
     *
     * @param url The URL to invoke.
     * @param body Body to include with the request.
     * @param responseType The type of response expected from the URL if it
     * completes successfully.
     * @param <R> The type of response to return.
     *
     * @return Response from the URL.
     */
    private <Q, R> ResponseEntity<R> post(final String url
        , final Q body
        , final Class<R> responseType) {
        return exchange(POST, url, body, responseType);
    }

    /**
     * Performs an HTTP POST request with
     * {@code application/x-www-form-urlencoded} as the content type, and
     * returns the response received as a result of the request.
     *
     * @param url The URL to invoke.
     * @param formData Parameters to include in the request body.
     * @param responseType The type of response expected from the URL if it
     * completes successfully.
     * @param <R> The type of response to return.
     *
     * @return Response from the URL.
     */
    private <R> ResponseEntity<R> postWithURLEncodedFormData(final String url
        , final Map<String, String> formData
        , final Class<R> responseType) {
        return exchange(POST, url, formData, responseType);
    }

    /**
     * Performs an HTTP PUT request and returns the response received as a
     * result of the request.
     *
     * @param url The URL to invoke.
     * @param body Body to include with the request.
     * @param responseType The type of response expected from the URL if it
     * completes successfully.
     * @param <R> The type of response to return.
     *
     * @return Response from the URL.
     */
    private <Q, R> ResponseEntity<R> put(final String url
        , final Q body
        , final Class<R> responseType) {
        return exchange(PUT, url, body, responseType);
    }

    /**
     * Performs an HTTP request and returns the response received as a
     * result of the request.
     *
     * @param method The HTTP method to use for the request.
     * @param url The URL to invoke.
     * @param body Optional body to include with the request.
     * @param responseType The type of response expected from the URL if it
     * completes successfully.
     * @param <R> The type of response to return.
     *
     * @return Response from the URL.
     */
    private <Q, R> ResponseEntity<R> exchange(final HttpMethod method
        , final String url
        , final Q body
        , final Class<R> responseType) {

        final var restTemplate = new RestTemplate();

        try {
            // Invoke the specified REST endpoint.
            return body != null
                   ? restTemplate.exchange(url, method
                , new HttpEntity<>(body, getHeaders()), responseType)
                   : restTemplate.exchange(url, method
                , new HttpEntity<>(getHeaders()), responseType);
        }
        catch (final HttpStatusCodeException e) {
            LOGGER.error("Invocation of REST endpoint [{}] resulted in error "
                             + "code [{}] and the following exception."
                , url
                , e.getStatusCode()
                , e);

            // If an HTTP error is thrown upon invoking the endpoint, the REST
            // template throws an exception and does not parse the response
            // body. For this reason, we forcibly parse the response body so
            // that the body is returned in full to the caller. We use the
            // underlying infrastructure for parsing the response body to
            // take advantage of the entire built-in resolution of response
            // content type.
            try (final var content = new ByteArrayClientHttpResponse(e.getResponseBodyAsByteArray(), e.getResponseHeaders())) {
                final var response = new HttpMessageConverterExtractor<>(responseType
                    , restTemplate.getMessageConverters())
                    .extractData(content);

                return new ResponseEntity<>(response, e.getStatusCode());
            }
            catch (final IOException i) {
                throw new RuntimeException(i);
            }
        }
    }

    /**
     * Gets headers to include with an HTTP request.
     *
     * @return A {@link MultiValueMap} containing HTTP headers.
     */
    private MultiValueMap<String, String> getHeaders() {
        final MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

        headers.add(ACCEPT, APPLICATION_JSON_VALUE);                                                                    // Accept: application/json
        headers.add(AUTHORIZATION, format("Bearer %s", settings.getAccessToken()));                                     // Accept: application/json
        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);                                                              // Content-Type: application/json

        return headers;
    }

    /**
     * A {@link ClientHttpResponse} that stores the entire response body as
     * an array of bytes.
     */
    static final class ByteArrayClientHttpResponse
        implements ClientHttpResponse {
        private final byte[] body;

        private final HttpHeaders headers;

        /**
         * Creates a new client response.
         *
         * @param body The response body as a byte array.
         * @param headers Headers received with the response.
         */
        ByteArrayClientHttpResponse(final byte[] body, final HttpHeaders headers) {
            this.body = body;
            this.headers = headers;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public InputStream getBody() {
            return new ByteArrayInputStream(body);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public HttpHeaders getHeaders() {
            return headers;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public HttpStatusCode getStatusCode() throws IOException {
            return HttpStatus.OK;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getStatusText() {
            return HttpStatus.OK.name();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void close() {
        }
    }
}

/**
 * Mapping metadata for an entity mapped to an Airtable table.
 */
class AirtableEntityMappingData {
    private final Map<String, AirtablePersistentProperty> fieldMappings;

    private final AirtablePersistentProperty idProperty;

    private final PreferredConstructor<?, ?> preferredConstructor;

    /**
     * Creates metadata for a persistent entity.
     *
     * @param preferredConstructor Preferred constructor method for the entity.
     * @param idProperty The identifier property for the entity.
     * @param fieldMappings Field mappings for the entity.
     */
    AirtableEntityMappingData(final PreferredConstructor<?, ?> preferredConstructor
        , final AirtablePersistentProperty idProperty
        , final Map<String, AirtablePersistentProperty> fieldMappings) {
        this.fieldMappings = fieldMappings;
        this.idProperty = idProperty;
        this.preferredConstructor = preferredConstructor;
    }

    /**
     * Gets field mappings for the entity.
     *
     * @return Field mappings for the entity.
     */
    public Map<String, AirtablePersistentProperty> getFieldMappings() {
        return fieldMappings;
    }

    /**
     * Gets identifier property for the entity.
     *
     * @return Identifier property for the entity.
     */
    public AirtablePersistentProperty getIdProperty() {
        return idProperty;
    }

    /**
     * Gets the preferred constructor method for the entity.
     *
     * @return The preferred constructor method for the entity.
     */
    public PreferredConstructor<?, ?> getPreferredConstructor() {
        return preferredConstructor;
    }
}
