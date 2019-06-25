package com.cgcg.base.interceptor;

import com.alibaba.fastjson.JSON;
import com.cgcg.base.context.SpringContextHolder;
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
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;
import java.net.URI;

/**
 * 结果集处理
 *
 * @auth zhicong.lin
 * @date 2019/6/25
 */
@Slf4j
@ControllerAdvice
public class ResponseDataHandler implements ResponseBodyAdvice {
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        final URI uri = request.getURI();
        final String path = uri.getPath();
        if (path.contains("/swagger") || path.equals("/error")
                || path.equals("/v2/api-docs") || path.contains("/webjars")) {
            return body;
        }
        if (returnType.hasMethodAnnotation(ExceptionHandler.class)) {
            //经过异常处理的结果类型，直接返回，不进行格式化
            return body;
        }
        //获取配置，是否开启格式化
        final Boolean formatResponseData = SpringContextHolder.getProperty("cgcg.format.response-data", Boolean.class);
        if (formatResponseData == null || !formatResponseData) {
            //没有开启格式化，直接返回
            return body;
        }
        return this.getFormatResult(body, selectedConverterType);
    }

    private Object getFormatResult(Object body, Class selectedConverterType) {
        final String className = SpringContextHolder.getProperty("cgcg.format.class-name");
        Class<?> formatClass = null;
        try {
            if (StringUtils.isBlank(className)) {
                formatClass = Result.class;
            } else {
                formatClass = Class.forName(className);
                if (formatClass == null) {
                    formatClass = Result.class;
                }
            }
        } catch (Exception e) {
            formatClass = Result.class;
            log.error("没有找到对应格式化的类名{}", className);
        }
        if (body != null && body.getClass() == formatClass) {
            //判断ResultMap类型，直接返回，不进行格式化
            return body;
        }
        try {
            final Method success = formatClass.getMethod("success", Object.class);
            final Object result = success.invoke(null, body);
            if (selectedConverterType == StringHttpMessageConverter.class) {
                //StringHttpMessageConverter 解析器，需要转成json字符串，不然会类型转换异常
                return JSON.toJSONString(result);
            }
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return body;
    }
}
