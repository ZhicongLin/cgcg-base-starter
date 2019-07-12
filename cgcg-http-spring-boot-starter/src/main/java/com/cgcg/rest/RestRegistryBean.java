package com.cgcg.rest;


import com.cgcg.rest.annotation.EnableRestClients;
import com.cgcg.rest.annotation.RestClient;
import com.cgcg.rest.proxy.RestCglibFactoryBean;
import com.cgcg.rest.proxy.RestJdkFactoryBean;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * java bean 注册中心
 *
 * @Author: ZhiCong Lin
 * @Date: 2018/8/8 16:48
 */
@Slf4j
@Setter
public class RestRegistryBean implements ImportBeanDefinitionRegistrar, EnvironmentAware {
    private Environment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        final Map<String, Object> annotationAttributes = annotationMetadata.getAnnotationAttributes(EnableRestClients.class.getName());
        final Set<Class<?>> restClientClasses = this.getScannerClass(annotationAttributes);
        this.registerBean(beanDefinitionRegistry, restClientClasses);
    }

    /**
     * 注册类到spring容器 .
     *
     * @Param: [beanDefinitionRegistry, restClientClasses]
     * @Return: void
     * @Author: ZhiCong Lin
     * @Date: 2018/8/13 17:48
     */
    private void registerBean(BeanDefinitionRegistry beanDefinitionRegistry, Set<Class<?>> restClientClasses) {
        for (Class beanClass : restClientClasses) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
            AbstractBeanDefinition definition = builder.getBeanDefinition();
            MutablePropertyValues mutablePropertyValues = new MutablePropertyValues();
            mutablePropertyValues.add("interfaceClass", beanClass);
            definition.setPropertyValues(mutablePropertyValues);
            //生成代理类
            final Boolean proxyModel = this.environment.getProperty("rest.proxy-target-class", Boolean.class);
            if (proxyModel == null || proxyModel) {
                definition.setBeanClass(RestCglibFactoryBean.class); //spring cglib 动态代理
            } else {
                definition.setBeanClass(RestJdkFactoryBean.class); //jdk 动态代理
            }
            definition.setAutowireCandidate(true);
            //生成bean名称
            final String javaBeanName = this.generationBeanName(beanClass.getSimpleName());
            log.debug("Generation Rest Client '{}' of type [{}].", javaBeanName, beanClass.getName());
            //注册到容器
            beanDefinitionRegistry.registerBeanDefinition(javaBeanName, definition);
        }
    }

    /**
     * 获取全部扫描类 .
     *
     * @Param: [annotationAttributes]
     * @Return: java.util.Set<java.lang.Class < ?>>
     * @Author: ZhiCong Lin
     * @Date: 2018/8/13 17:47
     */
    private Set<Class<?>> getScannerClass(Map<String, Object> annotationAttributes) {
        final Object[] scannerPkgs = new HashSet<>(Arrays.asList(this.getScannerPkgs(annotationAttributes))).toArray();
        final Reflections reflections = new Reflections(scannerPkgs);
        final Set<Class<?>> restClients = reflections.getTypesAnnotatedWith(RestClient.class);
        final Class<?>[] scanClass = (Class[]) annotationAttributes.get("basePackageClasses");
        if (scanClass != null) {
            restClients.addAll(Arrays.asList(scanClass));
        }
        return restClients;
    }

    /**
     * 获取全部的扫描包, 在没有配置的情况下，默认扫描 com 包 .
     *
     * @Param: [annotationAttributes]
     * @Return: java.lang.Object[]
     * @Author: ZhiCong Lin
     * @Date: 2018/8/13 17:47
     */
    private Object[] getScannerPkgs(Map<String, Object> annotationAttributes) {
        final String[] scanPkgs = (String[]) annotationAttributes.get("value");
        final Object[] properteisPkgs = this.environment.getProperty("rest.scan-package", Object[].class);
        if (properteisPkgs != null && scanPkgs != null) {
            Object[] allPkgs = Arrays.copyOf(properteisPkgs, properteisPkgs.length + scanPkgs.length);
            System.arraycopy(scanPkgs, 0, allPkgs, properteisPkgs.length, scanPkgs.length);
            return allPkgs;
        } else if (properteisPkgs == null && scanPkgs == null || scanPkgs != null && scanPkgs.length == 0) {
            return new Object[]{"com"};
        } else {
            return properteisPkgs != null ? properteisPkgs : scanPkgs;
        }
    }

    /**
     * 类名转成java bean名称 .
     *
     * @Param: [className]
     * @Return: java.lang.String
     * @Author: ZhiCong Lin
     * @Date: 2018/8/13 17:49
     */
    private String generationBeanName(String className) {
        return Character.isLowerCase(className.charAt(0)) ? className : Character.toLowerCase(className.charAt(0)) + className.substring(1);
    }
}