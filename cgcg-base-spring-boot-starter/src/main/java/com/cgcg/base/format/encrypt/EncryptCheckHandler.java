package com.cgcg.base.format.encrypt;

import com.cgcg.context.util.AnnotationUtil;
import org.springframework.core.MethodParameter;

import java.util.Objects;
/**
 * 加密校验工具类
 */
public class EncryptCheckHandler {

    /**
     * 是否需要加密校验方法
     */
    public static boolean processor(MethodParameter methodParameter) {
        final Class<?> declaringClass = Objects.requireNonNull(methodParameter.getMethod()).getDeclaringClass();
        final Encrypt encrypt = methodParameter.getMethodAnnotation(Encrypt.class);
        if (encrypt != null && encrypt.enable()) {
            return true;
        }
        final Encrypt annotation = AnnotationUtil.getAnnotation(declaringClass, Encrypt.class);
        return annotation != null && annotation.enable();
    }
}
