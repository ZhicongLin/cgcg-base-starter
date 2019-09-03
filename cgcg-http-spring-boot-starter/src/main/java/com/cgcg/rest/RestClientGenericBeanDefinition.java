package com.cgcg.rest;

import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;

@Getter
public class RestClientGenericBeanDefinition  {
    private AnnotationMetadata metadata;
    private Class<?> beanClass;
    private String beanClassName;
    @SneakyThrows
    public RestClientGenericBeanDefinition(MetadataReader metadataReader) {
        this.metadata = metadataReader.getAnnotationMetadata();
        this.beanClassName = this.metadata.getClassName();
        this.beanClass = ClassUtils.forName(this.beanClassName, metadataReader.getClass().getClassLoader());
    }

}
