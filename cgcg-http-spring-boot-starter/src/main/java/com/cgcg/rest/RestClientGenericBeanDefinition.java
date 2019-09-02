package com.cgcg.rest;

import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@Getter
public class RestClientGenericBeanDefinition extends GenericBeanDefinition implements AnnotatedBeanDefinition {
    private AnnotationMetadata metadata;
    private Class<?> beanClass;
    private MetadataReader metadataReader;
    public RestClientGenericBeanDefinition(MetadataReader metadataReader) {
        Assert.notNull(metadataReader, "MetadataReader must not be null");
        this.metadata = metadataReader.getAnnotationMetadata();
        this.metadataReader = metadataReader;
        this.setBeanClassName(this.metadata.getClassName());
        this.beanClass = transform();
    }

    @Nullable
    @Override
    public MethodMetadata getFactoryMethodMetadata() {
        return null;
    }

    /**
     * @return
     */
    @SneakyThrows
    private Class<?> transform() {
        return Class.forName(this.getBeanClassName());
    }
}
