package com.cgcg.base.encrypt;

import com.alibaba.fastjson.JSON;
import com.cgcg.base.enums.FormatProperty;
import com.cgcg.base.util.DES3Util;
import com.cgcg.base.util.ReflectionUtils;
import com.cgcg.base.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.net.URI;
import java.util.Objects;

/**
 * 结果集处理
 *
 * @auth zhicong.lin
 * @date 2019/6/25
 */
@Slf4j
@ControllerAdvice
public class ResponseBodyHandler implements ResponseBodyAdvice {
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        final boolean hasResponseBody = returnType.hasMethodAnnotation(ResponseBody.class);
        final Class<?> declaringClass = Objects.requireNonNull(returnType.getMethod()).getDeclaringClass();
        final RestController annotation = declaringClass.getAnnotation(RestController.class);
        final EncryptController encryptController = declaringClass.getAnnotation(EncryptController.class);
        return hasResponseBody || annotation != null || encryptController != null;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        final URI uri = request.getURI();
        final String path = uri.getPath();
        if (path.contains("/swagger-resources") || path.equals("/error") || path.equals("/v2/api-docs")) {
            return body;
        }
        if (returnType.hasMethodAnnotation(ExceptionHandler.class)) {
            //经过异常处理的结果类型，直接返回，不进行格式化
            return body;
        }
        //获取配置，是否开启格式化
        final Boolean formatResponseData = FormatProperty.DATA.getBoolean();
        if (formatResponseData == null || !formatResponseData) {
            //没有开启格式化，直接返回
            return encrypt(body);
        }
        final Object formatResult = this.getFormatResult(body, selectedConverterType);
        return encrypt(formatResult);
    }

    private Object getFormatResult(Object body, Class selectedConverterType) {
        final String className = FormatProperty.CLASS_NAME.getString();
        Class<?> formatClass = Result.class;
        try {
            if (StringUtils.isNotBlank(className)) {
                formatClass = Class.forName(className);
            }
        } catch (Exception e) {
            log.warn("[{}={}]配置错误", FormatProperty.CLASS_NAME.getKey(), className);
        }
        if (body != null && body.getClass() == formatClass) {
            //判断ResultMap类型，直接返回，不进行格式化
            return body;
        }
        try {
            final Object result = formatClass.newInstance();
            final String fmtField = StringUtils.isBlank(FormatProperty.PROPERTY.getString()) ? "data" : FormatProperty.PROPERTY.getString();
            ReflectionUtils.setFieldValue(result, fmtField, body);
            if (selectedConverterType == StringHttpMessageConverter.class) {
                //StringHttpMessageConverter 解析器，需要转成json字符串，不然会类型转换异常
                return JSON.toJSONString(result);
            }
            return result;
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        return body;
    }

    private Object encrypt(Object result) {
        final String encryptionKey = FormatProperty.des(FormatProperty.DES_RESULT);
        if (StringUtils.isBlank(encryptionKey)) {
            return result;
        }
        final String fmtField = StringUtils.isBlank(FormatProperty.PROPERTY.getString()) ? "data" : FormatProperty.PROPERTY.getString();
        final Object data = ReflectionUtils.getFieldValue(result, fmtField);
        if (data != null) {
            final String encryData = DES3Util.encryptMode(JSON.toJSONString(data), encryptionKey);
            ReflectionUtils.setFieldValue(result, fmtField, encryData);
        }
        return result;
    }
}
