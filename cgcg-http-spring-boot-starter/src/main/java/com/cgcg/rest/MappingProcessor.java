package com.cgcg.rest;

import com.cgcg.context.util.ReflectionUtils;
import com.cgcg.rest.annotation.DynamicMapping;
import com.cgcg.rest.annotation.LoadMapping;
import com.cgcg.rest.annotation.UpLoadMapping;
import lombok.*;
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
public final class MappingProcessor {
    private static final List<Class<?>> mappingAnnotation = Arrays.asList(RequestMapping.class, PostMapping.class, GetMapping.class, PutMapping.class,
            DeleteMapping.class, PatchMapping.class, DynamicMapping.class, LoadMapping.class, UpLoadMapping.class);

    /**
     * 获取方法动态资源映射信息
     *
     * @param method
     * @return MappingHandle
     */
    public static MappingHandle execute(Method method) {
        return execute(method.getDeclaredAnnotations(), null);
    }

    private static MappingHandle execute(Annotation[] annotations, String value) {
        for (Annotation annotation : annotations) {
            final Class<? extends Annotation> anType = annotation.annotationType();
            if (!mappingAnnotation.contains(anType)) {
                continue;
            }
            final String[] valueArr = (String[]) ReflectionUtils.invokeMethod(annotation, "value", new Class[]{}, new Object[]{});
            if (value == null && valueArr.length > 0) {
                value = valueArr[0];
            }
            try {
                final RequestMethod[] method = (RequestMethod[]) ReflectionUtils.invokeMethod(annotation, "method", new Class[]{}, new Object[]{});
                final HttpMethod httpMethod = HttpMethod.valueOf(method.length > 0 ? method[0].name() : RequestMethod.GET.name());
                return new MappingHandle(value, httpMethod);
            } catch (IllegalArgumentException e) {
                return execute(anType.getDeclaredAnnotations(), value);
            }
        }
        return null;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class MappingHandle {
        private String value;
        private HttpMethod httpMethod;
    }
}