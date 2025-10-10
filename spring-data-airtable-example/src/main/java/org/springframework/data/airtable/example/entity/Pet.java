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

package org.springframework.data.airtable.example.entity;

import org.springframework.data.airtable.example.type.PetType;
import org.springframework.data.airtable.mapping.Field;
import org.springframework.data.airtable.mapping.Table;
import org.springframework.data.annotation.Id;

/**
 * A pet.
 */
@Table(name = "Pets")
public class Pet {
    @Field(name = "Age in Years")
    private int age;

    @Id
    private String id;

    @Field(name = "Pet Name")
    private String name;

    @Field(name = "Pet Type")
    private PetType type;

    /**
     * Gets the pet's age.
     *
     * @return The pet's age.
     */
    public int getAge() {
        return age;
    }

    /**
     * Gets the unique identifier for the record.
     *
     * @return The unique identifier for the record.
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the name of the pet.
     *
     * @return The name of the pet.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the type of pet.
     *
     * @return The type of pet.
     */
    public PetType getType() {
        return type;
    }

    /**
     * Sets the pet's age.
     *
     * @param age The pet's age.
     */
    public void setAge(final int age) {
        this.age = age;
    }

    /**
     * Sets the unique identifier for the record.
     *
     * @param id The unique identifier for the record.
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Sets the name of the pet.
     *
     * @param name The name of the pet.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Sets the type of pet.
     *
     * @param type The type of pet.
     */
    public void setType(final PetType type) {
        this.type = type;
    }
}
