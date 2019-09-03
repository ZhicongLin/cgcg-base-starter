package com.cgcg.base.format.encrypt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cgcg.base.core.enums.FormatProperty;
import com.cgcg.context.util.DES3Util;
import com.cgcg.context.util.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 结果集加密处理
 *
 * @auth zhicong.lin
 * @date 2019/6/25
 */
@Slf4j
@Order(2)
@ControllerAdvice
public class ResponseBodyEncryptHandler implements ResponseBodyAdvice {
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return EncryptCheckHandler.processor(returnType);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body != null) {
            return encrypt(body, selectedConverterType);
        }
        return null;
    }

    private Object encrypt(Object result, Class selectedConverterType) {
        final String encryptionKey = FormatProperty.des(FormatProperty.DES_RESULT);
        if (StringUtils.isBlank(encryptionKey)) {
            return result;
        }
        final String fmtField = StringUtils.isBlank(FormatProperty.PROPERTY.getString()) ? "data" : FormatProperty.PROPERTY.getString();
        Object data;
        JSONObject jos = null;
        if (selectedConverterType.equals(StringHttpMessageConverter.class)) {
            try {
                jos = JSON.parseObject(result.toString());
                data = jos.get(fmtField);
            } catch (Exception e) {
                data = null;
            }
        } else {
            data = ReflectionUtils.getFieldValue(result, fmtField);
        }
        if (data != null) {
            final String encryData = DES3Util.encryptMode(JSON.toJSONString(data), encryptionKey);
            if (jos == null) {
                ReflectionUtils.setFieldValue(result, fmtField, encryData);
            } else {
                jos.put(fmtField, encryData);
                result = jos.toJSONString();
            }
        } else {
            final Class<?> formatClass = FormatProperty.getFormatClass();
            if (!(result.getClass().equals(formatClass))) {
                return DES3Util.encryptMode(JSON.toJSONString(result), encryptionKey);
            }
        }
        return result;
    }
}
