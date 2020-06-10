package com.cgcg.rest;

import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;

import lombok.Getter;
import lombok.SneakyThrows;

@Getter
public class RestClientGenericBeanDefinition {
    private final AnnotationMetadata metadata;
    private final Class<?> beanClass;
    private final String beanClassName;

    @SneakyThrows
    public RestClientGenericBeanDefinition(MetadataReader metadataReader) {
        this.metadata = metadataReader.getAnnotationMetadata();
        this.beanClassName = this.metadata.getClassName();
        this.beanClass = ClassUtils.forName(this.beanClassName, metadataReader.getClass().getClassLoader());
    }

}
