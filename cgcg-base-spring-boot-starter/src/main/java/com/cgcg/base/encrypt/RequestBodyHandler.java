package com.cgcg.base.encrypt;

import com.cgcg.base.enums.FormatProperty;
import com.cgcg.base.exception.EncryptionParamWrongException;
import com.cgcg.base.util.DES3Util;
import com.cgcg.base.util.HttpHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 请求体加密解密.
 *
 * @author zhicong.lin
 * @date 2019/6/29
 */
@Slf4j
@ControllerAdvice
public class RequestBodyHandler extends RequestBodyAdviceAdapter implements RequestBodyAdvice {

    @Override
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        final Encrypt encrypt = Objects.requireNonNull(methodParameter.getMethod()).getDeclaringClass().getAnnotation(Encrypt.class);
        final EncryptController controllerFlag = Objects.requireNonNull(methodParameter.getMethod()).getDeclaringClass().getAnnotation(EncryptController.class);
        final boolean methodFlag = methodParameter.hasMethodAnnotation(Encrypt.class);
        final boolean requestBodyFlag = methodParameter.hasParameterAnnotation(RequestBody.class);
        return (encrypt != null || controllerFlag != null || methodFlag) && requestBodyFlag;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) throws IOException {
        return new HttpInputMessage() {
            @Override
            public InputStream getBody() throws IOException {
                final String requestBodyString = HttpHelper.getStringBody(httpInputMessage.getBody());
                final String requestParam = DES3Util.decryptMode(requestBodyString, FormatProperty.des(FormatProperty.DES_PARAM));
                if (StringUtils.isBlank(requestParam)) {
                    throw new EncryptionParamWrongException(requestBodyString);
                }
                return new ByteArrayInputStream(requestParam.getBytes(StandardCharsets.UTF_8));
            }

            @Override
            public HttpHeaders getHeaders() {
                return httpInputMessage.getHeaders();
            }
        };
    }
}
