package com.cgcg.rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cgcg.context.util.ReflectionUtils;
import com.cgcg.rest.annotation.DynamicMapping;
import com.cgcg.rest.annotation.LoadMapping;
import com.cgcg.rest.annotation.UpLoadMapping;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 注解工具类.
 *
 * @author zhicong.lin
 * @date 2019/7/4
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MappingProcessor {
    private static final List<Class<?>> MAPPING_ANNOTATIONS = Arrays.asList(RequestMapping.class, PostMapping.class, GetMapping.class, PutMapping.class,
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
            final Class<? extends Annotation> annType = annotation.annotationType();
            if (MAPPING_ANNOTATIONS.contains(annType)) {
                final String[] valueArr = (String[]) ReflectionUtils.invokeMethod(annotation, "value", new Class[]{}, new Object[]{});
                if (value == null && valueArr.length > 0) {
                    value = valueArr[0];
                }
                try {
                    final RequestMethod[] method = (RequestMethod[]) ReflectionUtils.invokeMethod(annotation, "method", new Class[]{}, new Object[]{});
                    final HttpMethod httpMethod = HttpMethod.valueOf(method.length > 0 ? method[0].name() : RequestMethod.GET.name());
                    return new MappingHandle(value, httpMethod);
                } catch (IllegalArgumentException e) {
                    return execute(annType.getDeclaredAnnotations(), value);
                }
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