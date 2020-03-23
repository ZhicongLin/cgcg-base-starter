package com.cgcg.base.format;

import com.cgcg.context.util.HttpHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 请求体加密解密.
 *
 * @author zhicong.lin
 * @date 2019/6/29
 */
@Slf4j
@Order(-1)
@ControllerAdvice
public class RequestBodyHandler extends RequestBodyAdviceAdapter implements RequestBodyAdvice {

    @Override
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return methodParameter.hasParameterAnnotation(RequestBody.class);
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) throws IOException {
        final String requestBodyString = HttpHelper.getStringBody(httpInputMessage.getBody());
        log.debug("body [{}]", requestBodyString);
        return new CgCgInputStreamMessage(requestBodyString, httpInputMessage.getHeaders());
    }
}
