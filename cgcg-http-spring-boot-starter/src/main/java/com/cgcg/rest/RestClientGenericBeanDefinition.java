package com.cgcg.rest;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import lombok.Getter;
import lombok.SneakyThrows;

@Getter
public class RestClientGenericBeanDefinition {
    private final AnnotationMetadata metadata;
    private final Class<?> beanClass;
    private final String beanClassName;
    private final AbstractBeanDefinition definition;
    @SneakyThrows
    public RestClientGenericBeanDefinition(AnnotationMetadata metadata, AbstractBeanDefinition definition) {
        this.metadata = metadata;
        this.definition = definition;
        this.beanClassName = this.metadata.getClassName();
        this.beanClass = ClassUtils.forName(this.beanClassName, metadata.getClass().getClassLoader());
    }

}
