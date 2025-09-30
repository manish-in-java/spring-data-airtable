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

package org.springframework.data.airtable.repository.config;

import org.springframework.context.annotation.Import;

import org.springframework.data.airtable.core.AirtableTemplate;

import java.lang.annotation.*;

/**
 * Enables Airtable repositories.
 */
@Documented
@Import(AirtableRepositoryRegistrar.class)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EnableAirtableRepositories {
    /**
     * Base packages to scan for annotated components. Use
     * {@link #basePackageClasses()} for a type-safe alternative to string-based
     * package names.
     */
    String[] basePackages() default {};

    /**
     * Type-safe alternative to {@link #basePackages()} for specifying the
     * packages to scan for annotated components. The package of each class
     * specified will be scanned. Consider creating a special no-op marker
     * class or interface in each package that serves no purpose other than
     * being referenced by this attribute.
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * <p>
     * Name of the {@link AirtableTemplate} bean definition to use for
     * creating repositories.
     * </p>
     *
     * <p>
     * Defaults to {@code airtableTemplate}.
     * </p>
     */
    String airtableTemplate() default "airtableTemplate";
}
