package com.cgcg.rest;

import com.cgcg.rest.annotation.EnableRestClients;
import com.cgcg.rest.annotation.RestClient;
import com.cgcg.rest.proxy.RestCglibFactoryBean;
import com.cgcg.rest.proxy.RestJdkFactoryBean;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * RestClient Registrar SpringContext
 */
@Slf4j
@Setter
@NoArgsConstructor
public class RestClientsRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {
    private ResourceLoader resourceLoader;
    private Environment environment;


    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        final AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(RestClient.class);
        final ClassPathScanningCandidateComponentProvider scanner = this.getScanner();
        scanner.setResourceLoader(this.resourceLoader);
        scanner.addIncludeFilter(annotationTypeFilter);
        final Set<String> basePackages = this.getBasePackages(metadata);
        for (String basePackage : basePackages) {
            this.registerRestClients(scanner.findCandidateComponents(basePackage), registry);
        }
    }

    private void registerRestClients(Collection<BeanDefinition> candidateComponents, BeanDefinitionRegistry registry) {
        for (BeanDefinition candidateComponent : candidateComponents) {
            if (candidateComponent instanceof AnnotatedBeanDefinition) {
                final AnnotationMetadata metadata = ((AnnotatedBeanDefinition) candidateComponent).getMetadata();
                Assert.isTrue(metadata.isInterface(), "@RestClient can only be specified on an interface");
                this.registerRestClientBean(registry, metadata);
            }
        }
    }

    @SneakyThrows
    private void registerRestClientBean(BeanDefinitionRegistry registry, AnnotationMetadata metadata) {
        final Class<?> beanClass;
        try {
            beanClass = Class.forName(metadata.getClassName());
        } catch (ClassNotFoundException e) {
            log.error("Register ERROR [{}]", e.getMessage());
            return;
        }
        final Boolean proxyModel = this.environment.getProperty("rest.proxy-target-class", Boolean.class);
        final Class proxyClass = proxyModel == null || proxyModel ? RestCglibFactoryBean.class : RestJdkFactoryBean.class;
        final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(proxyClass);
        builder.addPropertyValue(Constant.PROXY_CLASS_KEY, beanClass);
        final AbstractBeanDefinition definition = builder.getBeanDefinition();
        definition.setAutowireCandidate(true);
        final Map<String, Object> attributes = metadata.getAnnotationAttributes(RestClient.class.getCanonicalName());
        if (attributes != null) {
            final Boolean enableFallback = this.environment.getProperty("rest.fallback.enable", Boolean.class);
            final Object fallback = attributes.get(Constant.PROXY_FALLBACK_KEY);
            if ((enableFallback == null || enableFallback) && fallback != Void.class) {
                final Object bean = ((Class<?>) fallback).newInstance();
                builder.addPropertyValue(Constant.PROXY_FALLBACK_BEAN_KEY, bean);
            }
        }
        final String clientName = this.getClientName(attributes, beanClass);
        final BeanDefinitionHolder holder = new BeanDefinitionHolder(definition, metadata.getClassName(), new String[]{clientName});
        //注册到容器
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    private ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                return beanDefinition.getMetadata().isIndependent() && !beanDefinition.getMetadata().isAnnotation();
            }
        };
    }

    private Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        final Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(EnableRestClients.class.getCanonicalName());
        final Set<String> basePackages = new HashSet<>();
        if (attributes != null) {
            basePackages.addAll(Arrays.asList((String[]) attributes.get(Constant.REST_CLIENT_VALUE)));
            basePackages.addAll(Arrays.asList((String[]) attributes.get(Constant.REST_CLIENT_BASE_PACKAGES)));
            final Class[] classes = (Class[]) attributes.get(Constant.REST_CLIENT_BASE_PACKAGE_CLASSES);
            for (Class clazz : classes) {
                basePackages.add(ClassUtils.getPackageName(clazz));
            }
        }
        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        return basePackages;
    }

    private String getClientName(Map<String, Object> client, Class clazz) {
        if (client != null) {
            final String value = (String) client.get(Constant.REST_CLIENT_VALUE);
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        final String className = clazz.getSimpleName();
        return Character.isLowerCase(className.charAt(0)) ? className : Character.toLowerCase(className.charAt(0)) + className.substring(1);
    }

}
