package com.cgcg.rest;

import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;

/**
 * Rest客户端 java bean 的代理对象类
 *
 * @author zhicong.lin
 */
@Data
public class RestClientGenericBeanDefinition {
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
