package com.cgcg.base.format.encrypt;

import com.cgcg.base.core.enums.FormatProperty;
import com.cgcg.base.core.exception.EncryptionParamWrongException;
import com.cgcg.base.format.CgCgInputStreamMessage;
import com.cgcg.context.util.DES3Util;
import com.cgcg.context.util.HttpHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
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
@ControllerAdvice
public class RequestBodyEncryptHandler extends RequestBodyAdviceAdapter implements RequestBodyAdvice {

    @Override
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        if (EncryptCheckHandler.processor(methodParameter)) {
            return methodParameter.hasParameterAnnotation(RequestBody.class);
        }
        return false;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) throws IOException {
        final String requestBodyString = HttpHelper.getStringBody(httpInputMessage.getBody());
        final String requestParam = DES3Util.decryptMode(requestBodyString, FormatProperty.des(FormatProperty.DES_PARAM));
        log.debug("body ==>[{}]", requestParam);
        if (StringUtils.isBlank(requestParam)) {
            throw new EncryptionParamWrongException(requestBodyString);
        }
        return new CgCgInputStreamMessage(requestParam, httpInputMessage.getHeaders());
    }
}
