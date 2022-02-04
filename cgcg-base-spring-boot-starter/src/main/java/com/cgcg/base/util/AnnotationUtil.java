package com.cgcg.base.util;

import io.swagger.annotations.Api;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Indexed;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.annotation.*;
import java.util.Arrays;
import java.util.List;

/**
 * 注解工具类.
 *
 * @author zhicong.lin
 * @date 2019/7/4
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AnnotationUtil {
    private static final List<Class<?>> IGNORE = Arrays.asList(Documented.class, Target.class, Retention.class,
            Controller.class, Component.class, Indexed.class, Inherited.class, Api.class);

    /**
     * 判断clazz注解是否包含了ResponseBody
     *
     * @param clazz
     * @return boolean
     * @author : zhicong.lin
     * @date : 2019/7/4 11:48
     */
    public static boolean hasResponseBody(Class<?> clazz) {
        return hasAnnotation(clazz, ResponseBody.class);
    }

    /**
     * 判断clazz的注解是否包含annotationType
     *
     * @param clazz
     * @return boolean
     * @author : zhicong.lin
     * @date : 2019/7/4 11:48
     */
    public static boolean hasAnnotation(Class<?> clazz, Class<? extends Annotation> annotationType) {
        final Annotation[] annotations = clazz.getAnnotations();
        for (Annotation annotation : annotations) {
            final Class<? extends Annotation> annoType = annotation.annotationType();
            if (annoType.equals(annotationType)) {
                return true;
            }
            boolean flag = false;
            if (!IGNORE.contains(annoType)) {
                flag = hasAnnotation(annoType, annotationType);
            }
            if (flag) {
                return true;
            }
        }
        return false;
    }
    /**
     * 获取clazz的包含annotationType
     *
     * @param clazz
     * @return boolean
     * @author : zhicong.lin
     * @date : 2019/7/4 11:48
     */
    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotationType) {
        final Annotation[] annotations = clazz.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotationType.isInstance(annotation)) {
                return (T) annotation;
            }
            final Class<? extends Annotation> annoType = annotation.annotationType();
            if (!IGNORE.contains(annoType)) {
                final T result = getAnnotation(annoType, annotationType);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
}
