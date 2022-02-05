package com.cgcg.rest;

import com.cgcg.rest.annotation.RestClient;
import com.cgcg.rest.proxy.RestCglibFactoryBean;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * Rest客户端 java bean 的代理对象类
 *
 * @author zhicong.lin
 */
@Setter
@Getter
public class RestClientGeneric {
    private MetadataReader metadataReader;
    private Class<?> beanClass;
    private String beanClassName;
    private String beanName;
    private AnnotationMetadata metadata;
    private AbstractBeanDefinition beanDefinition;
    private String parentName;

    public static BeanDefinitionHolder build(MetadataReader metadataReader) {
        RestClientGeneric definition = new RestClientGeneric(metadataReader);
        return new BeanDefinitionHolder(definition.getBeanDefinition(), definition.getBeanClassName(), new String[]{definition.getBeanName()});
    }

    @SneakyThrows
    private RestClientGeneric(MetadataReader metadataReader) {
        this.metadataReader = metadataReader;
        this.metadata = metadataReader.getAnnotationMetadata();
        this.beanClassName = this.getMetadata().getClassName();
        this.beanClass = ClassUtils.forName(this.beanClassName, metadataReader.getClass().getClassLoader());

        final AnnotationMetadata metadata = this.getMetadata();
        final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RestCglibFactoryBean.class);
        builder.addPropertyValue("interfaceClass", beanClass);

        beanDefinition = builder.getBeanDefinition();

        beanDefinition.setAutowireCandidate(true);

        final Map<String, Object> attributes = metadata.getAnnotationAttributes(RestClient.class.getCanonicalName());
        if (attributes != null) {
            final Object fallback = attributes.get("fallback");
            if (fallback != Void.class) {
                final Object bean = ((Class<?>) fallback).newInstance();
                builder.addPropertyValue("fallbackBean", bean);
            }
        }

        beanDefinition.setFactoryBeanName(beanClassName);
        if (attributes != null) {
            this.beanName = attributes.getOrDefault("value", beanClassName).toString();
        }
        if (!StringUtils.hasText(this.beanName) && beanClassName != null) {
            this.beanName = beanClassName;
        }

    }

}
