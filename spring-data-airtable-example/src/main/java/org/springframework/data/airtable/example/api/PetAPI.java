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

package org.springframework.data.airtable.example.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.airtable.example.entity.Pet;
import org.springframework.data.airtable.example.service.PetService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API for managing pets.
 */
@RequestMapping("/pets")
@RestController
class PetAPI {
    @Autowired
    private PetService service;

    /**
     * Gets a page of pets.
     *
     * @param page The page to find.
     *
     * @return A page of pets.
     */
    @GetMapping
    public PagedModel<Pet> getPets(final Pageable page) {
        return new PagedModel<>(service.getPets(page));
    }
}
