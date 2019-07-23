package com.cgcg.rest.param;

import com.cgcg.rest.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * http rest 请求参数.
 *
 * @Author: ZhiCong.Lin
 * @Create: 2018-08-09 09:07
 */
public class RestParamUtils {

    /**
     * 获取请求参数 .
     *
     * @Param: [method, args]
     * @Return: java.util.Map<java.lang.String, java.lang.String>
     * @Author: ZhiCong Lin
     * @Date: 2018/8/9 11:37
     */
    public static RestHandle<String, Object> getRestParam(Method method, Object[] args, String url) {
        final RestHandle<String, Object> restParam = new RestHandle<>();
        restParam.setUrl(url);
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            Object param = args[i];
            final Annotation[] paramAnnotation = parameterAnnotations[i];
            final RestParamVisitorImpl restParamVisitor = new RestParamVisitorImpl();
            for (Annotation annotation : paramAnnotation) {
                ReflectionUtils.invokeMethod(restParamVisitor, "visitor", new Class[]{annotation.annotationType(), Object.class, RestHandle.class}, new Object[]{annotation, param, restParam});
            }
        }
        return restParam;
    }

}
