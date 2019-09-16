package com.cgcg.base.format.encrypt;

import com.cgcg.base.util.AnnotationUtil;
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
        Encrypt encrypt = methodParameter.getMethodAnnotation(Encrypt.class);
        if (encrypt != null && encrypt.enable()) {
            return true;
        }
        final Class<?> declaringClass = Objects.requireNonNull(methodParameter.getMethod()).getDeclaringClass();
        encrypt = AnnotationUtil.getAnnotation(declaringClass, Encrypt.class);
        return encrypt != null && encrypt.enable();
    }
}
