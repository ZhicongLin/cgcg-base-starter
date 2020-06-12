package com.cgcg.rest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.cgcg.rest.annotation.EnableRestClients;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * RestClient Registrar SpringContext
 */
@Slf4j
@Setter
public class RestClientsRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {
    private Environment environment;

    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        log.debug("=============> Starting Cgcg Rest Clients Register <=============");
        //获取扫描包
        final Set<String> basePackages = this.getBasePackages(annotationMetadata);
        //新增扫描器
        final RestClientScannerConfigurer scanner = new RestClientScannerConfigurer(this.environment);
        //扫描
        final long start = System.currentTimeMillis();
        final Set<RestClientGenericBeanDefinition> restClients = scanner.findCandidateComponents(basePackages);
        for (RestClientGenericBeanDefinition candidateComponent : restClients) {
            this.registerRestClientBean(registry, candidateComponent);
        }
        log.debug("Finished Cgcg Rest Clients scanning in {}ms, Found {} clients interfaces.", (System.currentTimeMillis() - start), restClients.size());
        log.debug("=============> Finished Cgcg Rest Clients Register <=============");
    }

    @SneakyThrows
    private void registerRestClientBean(BeanDefinitionRegistry registry, RestClientGenericBeanDefinition raced) {
        final AbstractBeanDefinition definition = raced.getDefinition();
        final String factoryBeanName = Objects.requireNonNull(definition.getFactoryBeanName());
        registry.registerBeanDefinition(factoryBeanName, definition);
        RestClientRunner.add(factoryBeanName);
        log.debug("Creating shared instance of singleton bean '{}'", factoryBeanName);
    }

    private Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        final Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(EnableRestClients.class.getCanonicalName());
        final Set<String> basePackages = new HashSet<>();
        if (attributes != null) {
            basePackages.addAll(Arrays.asList((String[]) attributes.get(Constant.REST_CLIENT_VALUE)));
            basePackages.addAll(Arrays.asList((String[]) attributes.get(Constant.REST_CLIENT_BASE_PACKAGES)));
            final Class[] classes = (Class[]) attributes.get(Constant.REST_CLIENT_BASE_PACKAGE_CLASSES);
            Arrays.stream(classes).forEach(clazz -> basePackages.add(ClassUtils.getPackageName(clazz)));
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
