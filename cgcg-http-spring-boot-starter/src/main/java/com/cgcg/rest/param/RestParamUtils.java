package com.cgcg.rest.param;

import com.cgcg.rest.annotation.DinamicaMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
            Annotation[] paramAnnotation = parameterAnnotations[i];
            final RestParamVisitorImpl restParamVisitor = new RestParamVisitorImpl();
            String dinamicaUrl = checkDinamica(param, paramAnnotation, restParamVisitor, url);
            if (!dinamicaUrl.equals(url)) {
                restParam.setUrl(dinamicaUrl);
            }
            String resultUrl = checkPathVariable(param, paramAnnotation, restParamVisitor, restParam.getUrl());
            if (!resultUrl.equals(restParam.getUrl())) {
                restParam.setUrl(resultUrl);
            }
            for (Annotation annotation : paramAnnotation) {
                restParamVisitor.visitor(annotation, param, restParam);
            }
        }
        return restParam;
    }

    private static String checkDinamica(Object arg, Annotation[] paramAnnotation, RestParamVisitorImpl restParamVisitor, String url) {
        for (Annotation annotation : paramAnnotation) {
            if (annotation instanceof DinamicaMapping) {
                url = restParamVisitor.visitor((DinamicaMapping) annotation, arg, url);
            }
        }
        return url;
    }

    private static String checkPathVariable(Object arg, Annotation[] paramAnnotation, RestParamVisitorImpl restParamVisitor, String url) {
        for (Annotation annotation : paramAnnotation) {
            if (annotation instanceof PathVariable) {
                url = restParamVisitor.visitor((PathVariable) annotation, arg, url);
            }
        }
        return url;
    }

}
