package com.cgcg.rest;

import com.cgcg.rest.annotation.RestClient;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class RestClientsScanner extends ClassPathScanningCandidateComponentProvider {

    public RestClientsScanner() {
        final AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(RestClient.class);
        this.addIncludeFilter(annotationTypeFilter);
    }

    public Set<BeanDefinition> findCandidateComponents(Collection<String> basePackages) {
        final Set<BeanDefinition> definitions = new HashSet<>();
        for (String basePackage : basePackages) {
            definitions.addAll(super.findCandidateComponents(basePackage));
        }
        return definitions;
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isIndependent() && !beanDefinition.getMetadata().isAnnotation();
    }
}
