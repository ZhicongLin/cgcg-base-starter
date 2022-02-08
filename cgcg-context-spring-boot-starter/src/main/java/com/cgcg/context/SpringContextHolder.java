package com.cgcg.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * 以静态变量保存Spring ApplicationContext, 可在任何代码任何地方任何时候中取出ApplicaitonContext.
 *
 * @author zhicong.lin
 */
@Slf4j
@Component
public class SpringContextHolder implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    private static Environment environment;

    /**
     * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    public static <T> T getBean(String name) {
        @SuppressWarnings("unchecked")
        T bean = (T) applicationContext.getBean(name);
        return bean;
    }

    /**
     * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * 获取配置文件的配置值
     *
     * @param name
     * @param tClass
     * @return T
     * @author zhicong.lin
     * @date 2022/2/8 9:43
     */
    public static <T> T getProperty(String name, Class<T> tClass) {
        return environment.getProperty(name, tClass);
    }

    /**
     * 获取配置文件配置值
     *
     * @param name
     * @return java.lang.String
     * @author zhicong.lin
     * @date 2022/2/8 9:43
     */
    public static String getProperty(String name) {
        return environment.getProperty(name);
    }

    /**
     * 获取所有注解类的Map
     *
     * @param clazz
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author zhicong.lin
     * @date 2022/2/8 9:42
     */
    public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> clazz) {
        return applicationContext.getBeansWithAnnotation(clazz);
    }

    /**
     * 实现ApplicationContextAware接口的context注入函数, 将其存入静态变量.
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        log.info("Initializing SpringContextHolder");
        Assert.notNull(applicationContext, "SpringContextHolder Load Error");
        SpringContextHolder.applicationContext = applicationContext;
        environment = getBean(Environment.class);
    }
}
