package org.cgcg.redis.core.mq;


import org.cgcg.redis.core.annotation.EnableRedisMQ;
import org.cgcg.redis.core.annotation.RmqListener;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author: zhicong.lin
 * Date: 2022/1/12 08:08
 */
public class RedisMqBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        //是否使用默认的filter，使用默认的filter意味着只扫描那些类上拥有Component、Service、Repository或Controller注解的类。
        final boolean useDefaultFilters = false;
        final ClassPathScanningCandidateComponentProvider beanScanner = new ClassPathScanningCandidateComponentProvider(useDefaultFilters);
        final TypeFilter includeFilter = new AnnotationTypeFilter(RmqListener.class);
        beanScanner.addIncludeFilter(includeFilter);

        final Set<BeanDefinition> beanDefinitions = new HashSet<>();
        for (String basePackage : getBasePackages(annotationMetadata)) {
            beanDefinitions.addAll(beanScanner.findCandidateComponents(basePackage));
        }

        for (BeanDefinition beanDefinition : beanDefinitions) {
            final Map<String, Object> attributes = ((ScannedGenericBeanDefinition) beanDefinition).getMetadata().getAnnotationAttributes(RmqListener.class.getName());
            final String beanClassName = beanDefinition.getBeanClassName();
            if (attributes != null) {
                final String beanName = attributes.getOrDefault("value", beanClassName).toString();
                beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);
            } else if (beanClassName != null) {
                beanDefinitionRegistry.registerBeanDefinition(beanClassName, beanDefinition);
            }
        }
    }

    private Set<String> getBasePackages(AnnotationMetadata annotationMetadata) {
        final Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(EnableRedisMQ.class.getCanonicalName());

        final Set<String> basePackages = new HashSet<>();
        // 指定包名
        if (!Objects.isNull(attributes)) {
            final String[] packages = (String[]) attributes.get("packages");
            for (String pkg : packages) {
                if (StringUtils.hasText(pkg)) {
                    basePackages.add(pkg);
                }
            }
        }
        // 如果没有指定包名，则扫描注解所在类的包名
        if (basePackages.size() == 0) {
            basePackages.add(ClassUtils.getPackageName(annotationMetadata.getClassName()));
        }

        return basePackages;
    }
}
