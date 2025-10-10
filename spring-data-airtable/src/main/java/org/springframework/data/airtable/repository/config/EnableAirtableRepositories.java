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

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.airtable.repository.support.AirtableRepositoryFactoryBean;
import org.springframework.data.repository.config.DefaultRepositoryBaseClass;

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
     * <p>
     * Configures the name of the Airtable client bean definition to use for
     * creating repositories. The Spring context must have a bean of this name
     * for Airtable repositories to be initialized successfully.
     * </p>
     *
     * <pre>{@code
     * Example
     * -------------------------------------------------------------------------
     * @Configuration
     * class AirtableConfig {
     *     @Bean(name = "airtableOperations")
     *     AirtableOperations airtableOperations() {
     *         ...
     *     }
     * }
     * }</pre>
     *
     * <p>
     * Defaults to {@code airtableOperations}.
     * </p>
     */
    String airtableOperations() default "airtableOperations";

    /**
     * Type-safe alternative to {@link #basePackages()} for specifying the
     * packages to scan for annotated components. The package of each class
     * specified will be scanned. Consider creating a special no-op marker class
     * or interface in each package that serves no purpose other than
     * being referenced by this attribute.
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * Base packages to scan for annotated components. Use
     * {@link #basePackageClasses()} for a type-safe alternative to
     * string-based package names.
     */
    String[] basePackages() default {};

    /**
     * Specifies which types are not eligible for component scanning.
     */
    ComponentScan.Filter[] excludeFilters() default {};

    /**
     * Specifies which types are eligible for component scanning. Further
     * narrows the set of candidate components from everything in
     * {@link #basePackages()} to everything in the base packages that matches
     * the given filter or
     * filters.
     */
    ComponentScan.Filter[] includeFilters() default {};

    /**
     * Configures the location of where to find the Spring Data named queries
     * properties file.
     */
    String namedQueriesLocation() default "";

    /**
     * <p>
     * Configure the repository base class to use to create repository proxies
     * for this particular configuration.
     * </p>
     * <p>
     * Default is {@link DefaultRepositoryBaseClass}.
     * </p>
     */
    Class<?> repositoryBaseClass() default DefaultRepositoryBaseClass.class;

    /**
     * <p>
     * Configures the {@link FactoryBean} class to use for each repository
     * instance.
     * </p>
     *
     * <p>
     * Defaults to {@link AirtableRepositoryFactoryBean}.
     * </p>
     */
    Class<?> repositoryFactoryBeanClass() default AirtableRepositoryFactoryBean.class;

    /**
     * <p>
     * Configures the suffix to use when looking up custom repository
     * implementations.
     * </p>
     * <p>
     * <p>
     * Defaults to {@literal Impl}. So for a repository named
     * {@code PersonRepository} the corresponding implementation class will be
     * looked up scanning for {@code PersonRepositoryImpl}.
     * </p>
     */
    String repositoryImplementationPostfix() default "Impl";

    /**
     * Alias for {@link #basePackages()} attribute.
     */
    String[] value() default {};
}
