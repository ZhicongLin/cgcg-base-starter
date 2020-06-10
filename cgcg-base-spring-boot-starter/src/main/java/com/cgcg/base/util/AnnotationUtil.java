package com.cgcg.base.util;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Indexed;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
     * 判断clzz注解是否包含了ResponseBody
     *
     * @auth zhicong.lin
     * @date 2019/7/4
     */
    public static boolean hasResponseBody(Class clzz) {
        return hasAnnotation(clzz, ResponseBody.class);
    }

    /**
     * 判断clzz的注解是否包含annotationType
     *
     * @auth zhicong.lin
     * @date 2019/7/4
     */
    public static boolean hasAnnotation(Class clzz, Class<? extends Annotation> annotationType) {
        final Annotation[] annotations = clzz.getAnnotations();
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

    @SuppressWarnings({"unchecked"})
    public static <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotationType) {
        final Annotation[] annotations = clazz.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotationType.isInstance(annotation)) {
                return (T) annotation;
            }
            final Class<? extends Annotation> annType = annotation.annotationType();
            if (!IGNORE.contains(annType)) {
                final T result = getAnnotation(annType, annotationType);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
}
