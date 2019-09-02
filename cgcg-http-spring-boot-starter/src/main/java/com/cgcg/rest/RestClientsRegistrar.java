package com.cgcg.rest;

import com.cgcg.rest.annotation.EnableRestClients;
import com.cgcg.rest.annotation.RestClient;
import com.cgcg.rest.proxy.RestCglibFactoryBean;
import com.cgcg.rest.proxy.RestJdkFactoryBean;
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
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * RestClient Registrar SpringContext
 */
@Slf4j
@Setter
public class RestClientsRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {
    private Environment environment;

    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        final long start = System.currentTimeMillis();
        //获取扫描包
        final Set<String> basePackages = this.getBasePackages(annotationMetadata);
        //新增扫描器
        final RestClientScannerConfigurer scanner = new RestClientScannerConfigurer(this.environment);
        //扫描
        final Set<BeanDefinition> restClients = scanner.findCandidateComponents(basePackages);
        for (BeanDefinition candidateComponent : restClients) {
            if (candidateComponent instanceof AnnotatedBeanDefinition) {
                final AnnotationMetadata metadata = ((AnnotatedBeanDefinition) candidateComponent).getMetadata();
                this.registerRestClientBean(registry, metadata);
            }
        }
        log.debug("Finished Cgcg Rest Clients scanning in {}ms, Found {} clients interfaces", (System.currentTimeMillis() - start), restClients.size());
    }

    @SneakyThrows
    private void registerRestClientBean(BeanDefinitionRegistry registry, AnnotationMetadata metadata) {
        final Class<?> beanClass;
        try {
            beanClass = Class.forName(metadata.getClassName());
        } catch (ClassNotFoundException e) {
            log.error("Register error [{}]", e.getMessage());
            return;
        }
        final Environment env = this.environment;
        final Boolean proxyModel = env.getProperty("rest.proxy-target-class", Boolean.class);
        final Class proxyClass = proxyModel == null || proxyModel ? RestCglibFactoryBean.class : RestJdkFactoryBean.class;
        final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(proxyClass); // BeanDefinitionBuilder.genericBeanDefinition(proxyClass);
        builder.addPropertyValue(Constant.PROXY_CLASS_KEY, beanClass);
        final AbstractBeanDefinition definition = builder.getBeanDefinition();
        definition.setAutowireCandidate(true);
        final Map<String, Object> attributes = metadata.getAnnotationAttributes(RestClient.class.getCanonicalName());
        if (attributes != null) {
            final Boolean enableFallback = env.getProperty("rest.fallback.enable", Boolean.class);
            final Object fallback = attributes.get(Constant.PROXY_FALLBACK_KEY);
            if ((enableFallback == null || enableFallback) && fallback != Void.class) {
                final Object bean = ((Class<?>) fallback).newInstance();
                builder.addPropertyValue(Constant.PROXY_FALLBACK_BEAN_KEY, bean);
            }
        }
        definition.setFactoryBeanName(beanClass.getName());
        final String clientName = this.getClientName(attributes, beanClass);
        final BeanDefinitionHolder holder = new BeanDefinitionHolder(definition, metadata.getClassName(), new String[]{clientName});
        //注册到容器
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
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
