package com.cgcg.base.interceptor;

import com.alibaba.fastjson.JSON;
import com.cgcg.base.context.SpringContextHolder;
import com.cgcg.base.vo.ResultMap;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
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
        if (body instanceof ResultMap) {
            //判断ResultMap类型，直接返回，不进行格式化
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
        //格式化数据
        final ResultMap<Object> success = ResultMap.success(body);
        if (selectedConverterType == StringHttpMessageConverter.class) {
            //StringHttpMessageConverter 解析器，需要转成json字符串，不然会类型转换异常
            return JSON.toJSONString(success);
        }
        return success;
    }
}
