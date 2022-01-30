package com.cgcg.base.format;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.Resource;
import java.util.List;

/**
 * 结果集格式化处理
 *
 * @author zhicong.lin
 * @date 2019/6/25
 */
@Slf4j
@Order(1)
@ControllerAdvice
public class ResponseBodyFormatHandler2<T> implements ResponseBodyAdvice<T> {
    private static final String SWAGGER_RESOURCES = "/swagger-resources";
    private static final String ERROR = "/error";
    private static final String DOCS = "/v2/api-docs";
    private static final String DOCS_EXT = "/v2/api-docs-ext";
    @Resource
    private List<String> responseDataIgnore;


    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return false;
    }

    @Override
    public T beforeBodyWrite(T body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        return body;
    }
}
