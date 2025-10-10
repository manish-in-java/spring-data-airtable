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

package org.springframework.data.airtable.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.airtable.example.entity.Pet;
import org.springframework.data.airtable.example.repository.PetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Provides business operations for pets.
 */
@Service
public class PetService {
    @Autowired
    private PetRepository repository;

    /**
     * Gets a page of pets.
     *
     * @param page The page to find.
     *
     * @return A page of pets.
     */
    public Page<Pet> getPets(final Pageable page) {
        return repository.findAll(page);
    }
}
