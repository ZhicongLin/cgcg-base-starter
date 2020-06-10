package com.cgcg.context.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.aopalliance.intercept.MethodInvocation;

import lombok.extern.slf4j.Slf4j;

/**
 * Description:
 *
 * @author linzc
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本           修改人       修改日期         修改内容
 * 2020/6/10.1       linzc    2020/6/10           Create
 * </pre>
 * @date 2020/6/10
 */
@Slf4j
public class AnnotationUtils {

    /**
     * 判断方法是否有注解
     *
     * @param bean
     * @param clazz
     * @param <T>
     */
    public static <T extends Annotation> boolean existMethodAnn(Object bean, Class<T> clazz) {
        if (bean == null) {
            return false;
        }
        try {
            final Class<?>[] interfaces = SpringProxyUtils.findInterfaces(bean);
            final Class<?> targetClass = SpringProxyUtils.findTargetClass(bean);
            return existMethodAnn(clazz, targetClass) || existMethodAnn(clazz, interfaces);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 判断方法是否有注解
     *
     * @param clazz
     * @param beanClazz
     * @param <T        extends Annotation>
     */
    private static <T extends Annotation> Boolean existMethodAnn(Class<T> clazz, Class<?>... beanClazz) {
        for (Class<?> clz : beanClazz) {
            final Method[] methods = clz.getMethods();
            final long count = Arrays.stream(methods).filter(method -> method.getDeclaredAnnotation(clazz) != null).count();
            if (count > 0) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * 获取指定注解
     * @param methodInvocation
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T extends Annotation> T getAnnotation(MethodInvocation methodInvocation, Class<T> clazz) {
        final Method method = methodInvocation.getMethod();
        return method.getDeclaredAnnotation(clazz);
    }
}