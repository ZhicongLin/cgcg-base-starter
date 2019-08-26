package com.cgcg.base.format;

import com.alibaba.fastjson.JSON;
import com.cgcg.base.core.enums.FormatProperty;
import com.cgcg.base.util.AnnotationUtil;
import com.cgcg.base.util.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.Resource;
import java.net.URI;
import java.util.List;
import java.util.Objects;

/**
 * 结果集格式化处理
 *
 * @auth zhicong.lin
 * @date 2019/6/25
 */
@Slf4j
@Order(1)
@ControllerAdvice
public class ResponseBodyFormatHandler implements ResponseBodyAdvice {
    @Resource
    private List<String> responseDataIgnore;
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        final Class<?> declaringClass = Objects.requireNonNull(returnType.getMethod()).getDeclaringClass();
        return returnType.hasMethodAnnotation(ResponseBody.class) || AnnotationUtil.hasResponseBody(declaringClass);
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
        if (formatResponseData == null || !formatResponseData || this.responseDataIgnore.contains(path)) {
            //没有开启格式化，直接返回
            return body;
        }
        return this.getFormatResult(body, selectedConverterType);
    }

    private Object getFormatResult(Object body, Class selectedConverterType) {
        final Class<?> formatClass = FormatProperty.getFormatClass();
        if (body != null && body.getClass().getName().equals(formatClass.getName())) {
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
}
