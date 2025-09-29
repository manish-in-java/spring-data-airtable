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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.airtable.domain.ListRecordsResponse;
import org.springframework.data.airtable.repository.support.AirtableEntityInformation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public final class AirtableTemplate implements AirtableOperations {
    private static final String BASE_URL = "https://api.airtable.com/v0";

    private static final Logger LOGGER = LoggerFactory.getLogger(AirtableTemplate.class);

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
     * {@inheritDoc}
     */
    @Override
    public <T> Page<T> list(final AirtableEntityInformation<T> type, final Pageable page) {
        // Invoke the Airtable List Records API.
        final var response = get(getEndpoint(type.getTableName()), ListRecordsResponse.class);

        return null;
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
     * Performs an HTTP GET request and returns the response received as an
     * array of bytes.
     *
     * @param url The URL to invoke.
     *
     * @return Response from the URL as a byte array.
     */
    private byte[] get(final String url) {
        return get(url, byte[].class).getBody();
    }

    /**
     * Performs an HTTP GET request and returns the response received as a
     * result of the request.
     *
     * @param url The URL to invoke.
     * @param responseType The type of response expected from the URL if it
     * completes successfully.
     * @param <R> The type of response to return.
     *
     * @return Response from the URL.
     */
    private <R> ResponseEntity<R> get(final String url
        , final Class<R> responseType) {
        return get(url, null, responseType);
    }

    /**
     * Performs an HTTP GET request and returns the response received as a
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
    private <Q, R> ResponseEntity<R> get(final String url
        , final Q body
        , final Class<R> responseType) {
        return exchange(GET, url, body, responseType);
    }

    /**
     * Performs an HTTP GET request and returns the response received as a
     * result of the request.
     *
     * @param url The URL to invoke.
     * @param request Parameters to include in the request URL.
     * @param responseType The type of response expected from the URL if it
     * completes successfully.
     * @param <R> The type of response to return.
     *
     * @return Response from the URL.
     */
    private <R> ResponseEntity<R> get(final String url
        , final ParameterizedRequest request
        , final Class<R> responseType) {
        final var urlBuilder = UriComponentsBuilder.fromHttpUrl(url);

        final Map<String, ?> parameters = Optional.ofNullable(request)
                                                  .map(ParameterizedRequest::getParameters)
                                                  .orElseGet(Collections::emptyMap);

        parameters.keySet()
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
        return format("%s/%s/%s", BASE_URL, settings.getBaseId(), table);
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
     * Contract for a request containing parameters to be included in the
     * request URL.
     */
    public interface ParameterizedRequest {
        /**
         * Gets parameters to included in the request URL.
         *
         * @return A map containing the parameters to include as key-value
         * pairs.
         */
        Map<String, ?> getParameters();
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
