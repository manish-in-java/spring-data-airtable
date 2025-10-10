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

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.data.airtable.mapping.Table;
import org.springframework.data.airtable.repository.AirtableRepository;
import org.springframework.data.airtable.repository.support.AirtableRepositoryFactoryBean;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;
import org.springframework.data.repository.config.XmlRepositoryConfigurationSource;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

/**
 * {@link RepositoryConfigurationExtension} for Airtable.
 */
public class AirtableRepositoryConfigurationExtension extends RepositoryConfigurationExtensionSupport {
    /**
     * {@inheritDoc}
    @Override
     */
    public String getModuleName() {
        return "Airtable";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getModulePrefix() {
        return "airtable";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRepositoryFactoryBeanClassName() {
        return AirtableRepositoryFactoryBean.class.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postProcess(final BeanDefinitionBuilder builder, final AnnotationRepositoryConfigurationSource config) {
        builder.addPropertyReference("airtableOperations", "airtableOperations");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postProcess(final BeanDefinitionBuilder builder, final XmlRepositoryConfigurationSource config) {
        builder.addPropertyReference("airtableOperations", config.getElement().getAttribute("airtable-operations-ref"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Collection<Class<? extends Annotation>> getIdentifyingAnnotations() {
        return Collections.singleton(Table.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Collection<Class<?>> getIdentifyingTypes() {
        return Collections.singleton(AirtableRepository.class);
    }
}
