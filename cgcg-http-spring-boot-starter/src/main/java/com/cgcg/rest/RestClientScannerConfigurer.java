package com.cgcg.rest;

import com.cgcg.rest.annotation.RestClient;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Setter
@Getter
public class RestClientScannerConfigurer {
    private static final String RESOURCE_PATTERN = "**/*.class";
    private static final String CLASSPATH_ALL_URL_PREFIX = "classpath*:";
    private AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(RestClient.class);
    private ResourceLoader resourceLoader = new DefaultResourceLoader();
    private MetadataReaderFactory readerFactory = new SimpleMetadataReaderFactory(resourceLoader);
    private Environment environment;

    public RestClientScannerConfigurer(Environment environment) {
        this.environment = environment;
    }

    public Set<RestClientGenericBeanDefinition> findCandidateComponents(Collection<String> basePackages) {
        try {
            final Set<RestClientGenericBeanDefinition> definitions = new HashSet<>();
            for (String basePackage : basePackages) {
                definitions.addAll(this.findCandidateClasses(basePackage));
            }
            return definitions;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取符合要求的Controller名称
     *
     * @param basePackage
     * @return
     * @throws IOException
     */
    public Set<RestClientGenericBeanDefinition> findCandidateClasses(String basePackage) throws IOException {
        final Set<RestClientGenericBeanDefinition> candidates = new HashSet<>();
        final String packageSearchPath = CLASSPATH_ALL_URL_PREFIX + resolveBasePackage(basePackage) + '/' + RESOURCE_PATTERN;
        final Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(packageSearchPath);
        for (Resource resource : resources) {
            final MetadataReader reader = readerFactory.getMetadataReader(resource);
            if (annotationTypeFilter.match(reader, readerFactory)) {
                final RestClientGenericBeanDefinition sbd = new RestClientGenericBeanDefinition(reader);
                sbd.setResource(resource);
                sbd.setSource(resource);
                candidates.add(sbd);
            }
        }
        return candidates;
    }

    /**
     * 用"/"替换包路径中"."
     *
     * @param basePackage
     * @return
     */
    protected String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(this.getEnvironment().resolveRequiredPlaceholders(basePackage));
    }
}