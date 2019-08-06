package com.cgcg.base.util;

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
    private static List<Class<?>> ignore = Arrays.asList(Documented.class, Target.class, Retention.class,
            Controller.class, Component.class, Indexed.class, Inherited.class);

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
            if (!ignore.contains(annoType)) {
                flag = hasAnnotation(annoType, annotationType);
            }
            if (flag) {
                return true;
            }
        }
        return false;
    }

    public static <T extends Annotation> T getAnnotation(Class clzz, Class<T> annotationType) {
        final Annotation[] annotations = clzz.getAnnotations();
        for (Annotation annotation : annotations) {
            final Class<? extends Annotation> annoType = annotation.annotationType();
            if (annoType.equals(annotationType)) {
                return (T) annotation;
            }
            if (!ignore.contains(annoType)) {
                return getAnnotation(annoType, annotationType);
            }
        }
        return null;
    }
}
