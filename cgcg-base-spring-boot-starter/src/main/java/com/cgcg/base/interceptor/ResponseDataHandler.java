package com.cgcg.base.interceptor;

import com.cgcg.base.context.SpringContextHolder;
import com.cgcg.base.vo.ResultMap;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 结果集处理
 * @auth zhicong.lin
 * @date 2019/6/25
 */
@ControllerAdvice
public class ResponseDataHandler implements ResponseBodyAdvice {
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        final Boolean formatResponseData = SpringContextHolder.getProperty("cgcg.format.response-data", Boolean.class);
        if (formatResponseData == null || !formatResponseData) {
            return body;
        }
        if (returnType.hasMethodAnnotation(ExceptionHandler.class)) {
            //处理异常，可以再添加一个异常处理的类，用于处理异常返回格式
            return body;
        }
        if (body instanceof ResultMap) {
            return body;
        }
        return ResultMap.success(body);
    }
}
