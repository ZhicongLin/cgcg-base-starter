package com.cgcg.rest;

import com.cgcg.rest.annotation.DinamicaMapping;
import com.cgcg.rest.annotation.LoadMapping;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
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
    private static List<Class<?>> mappingAnnotation = Arrays.asList(PostMapping.class, GetMapping.class, PutMapping.class,
            DeleteMapping.class, PatchMapping.class, LoadMapping.class, RequestMapping.class, DinamicaMapping.class);

    public static MappingHandle buildMappingHandle(Method method) {
        return buildMappingHandle(method.getDeclaredAnnotations(), null);
    }

    private static MappingHandle buildMappingHandle(Annotation[] annotations, String value) {
        for (Annotation annotation : annotations) {
            final Class<? extends Annotation> annoType = annotation.annotationType();
            if (mappingAnnotation.contains(annoType)) {
                final String[] valueArr = (String[])ReflectionUtils.invokeMethod(annotation, "value", new Class[]{}, new Object[]{});
                if (value == null && valueArr.length > 0) {
                    value = valueArr[0];
                }
                try {
                    final RequestMethod[] method = (RequestMethod[])ReflectionUtils.invokeMethod(annotation, "method", new Class[]{}, new Object[]{});
                    final HttpMethod httpMethod = HttpMethod.valueOf(method.length > 0 ? method[0].name() : RequestMethod.GET.name());
                    return new MappingHandle(value, httpMethod);
                } catch (IllegalArgumentException e) {
                    return buildMappingHandle(annoType.getDeclaredAnnotations(), value);
                }
            }
        }
        return null;
    }

    @Data
    @AllArgsConstructor
    public static class MappingHandle {
        private String value;
        private HttpMethod httpMethod;
    }
}