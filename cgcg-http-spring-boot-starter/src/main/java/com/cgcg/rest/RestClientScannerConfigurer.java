package com.cgcg.rest;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import com.cgcg.rest.annotation.RestClient;
import com.cgcg.rest.proxy.RestCglibFactoryBean;
import com.cgcg.rest.proxy.RestJdkFactoryBean;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
@Getter
public class RestClientScannerConfigurer {
    private static final String RESOURCE_PATTERN = "/**/*.class";
    private static final String CLASSPATH_ALL_URL_PREFIX = "classpath*:";
    private Environment environment;

    private ResourcePatternResolver resourcePatternResolver;
    private MetadataReaderFactory metadataReaderFactory;
    private String resourcePattern = "**/*.class";

    public RestClientScannerConfigurer(Environment environment) {
        this.environment = environment;
        this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
        this.metadataReaderFactory = new CachingMetadataReaderFactory();
    }

    public Set<RestClientGenericBeanDefinition> findCandidateComponents(Collection<String> basePackages) {
        Set<RestClientGenericBeanDefinition> beanDefinitions = new HashSet<>();
        basePackages.forEach(pkg -> scanRestClients(beanDefinitions, pkg));
        return beanDefinitions;
    }

    /**
     * 扫描包，并加载Bean
     * @param beanDefinitions
     * @param basePackage
     */
    private void scanRestClients(Set<RestClientGenericBeanDefinition> beanDefinitions, String basePackage) {
        try {
            final Resource[] resources = getResourcePatternResolver().getResources(this.getSourcePath(basePackage));
            for (Resource resource : resources) {
                if (!resource.isReadable()) {
                    log.trace("Ignored because not readable: " + resource);
                    continue;
                }
                final RestClientGenericBeanDefinition beanDefinition = this.loadBeanDefinition(resource);
                if (beanDefinition == null) {
                    continue;
                }
                beanDefinitions.add(beanDefinition);
            }
        } catch (IOException ex) {
            throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
        }
    }

    /**
     * 加载bean
     * @param resource
     * @return
     */
    private RestClientGenericBeanDefinition loadBeanDefinition(Resource resource) {
        try {
            MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(resource);
            final AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
            final boolean isRestClient = annotationMetadata.hasAnnotation(RestClient.class.getName());
            if (!isRestClient) {
                return null;
            }
            final Boolean proxyModel = this.environment.getProperty("cgcg.rest.proxy-target-class", Boolean.class);
            final Class proxyClass = proxyModel == null || proxyModel ? RestCglibFactoryBean.class : RestJdkFactoryBean.class;
            final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(proxyClass);
            final Class beanClass = Class.forName(metadataReader.getClassMetadata().getClassName());
            builder.addPropertyValue(Constant.PROXY_CLASS_KEY, beanClass);
            final AbstractBeanDefinition definition = builder.getBeanDefinition();
            definition.setAutowireCandidate(true);
            final Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(RestClient.class.getCanonicalName());
            if (attributes != null) {
                final Boolean enableFallback = this.environment.getProperty("cgcg.rest.fallback.enable", Boolean.class);
                final Object fallback = attributes.get(Constant.PROXY_FALLBACK_KEY);
                if ((enableFallback == null || enableFallback) && fallback != Void.class) {
                    final Object bean = ((Class<?>) fallback).newInstance();
                    builder.addPropertyValue(Constant.PROXY_FALLBACK_BEAN_KEY, bean);
                }
            }
            final String className = metadataReader.getClassMetadata().getClassName();
            definition.setFactoryBeanName(className);
            return new RestClientGenericBeanDefinition(annotationMetadata, definition);
        } catch (Throwable ex) {
            throw new BeanDefinitionStoreException(
                    "Failed to read rest client class: " + resource, ex);
        }
    }

    /**
     * 用"/"替换包路径中"."
     *
     * @param basePackage
     * @return
     */
    private String getSourcePath(String basePackage) {
        final String path = ClassUtils.convertClassNameToResourcePath(this.getEnvironment().resolveRequiredPlaceholders(basePackage));
        return ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + path + RESOURCE_PATTERN;
    }
}