package com.cgcg.base.core.enums;

import com.cgcg.base.format.Result;
import com.cgcg.context.SpringContextHolder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 格式化配置文件的枚举类.
 *
 * @author zhicong.lin
 * @date 2019/6/27
 */
@Slf4j
@Getter
public enum FormatProperty {
    //数据格式化配置
    DATA("cgcg.format.response-data"),
    //数据格式化类配置
    CLASS_NAME("cgcg.format.class-name"),
    PROPERTY("cgcg.format.property"),
    //数据加密密钥配置
    DES_ROOT("cgcg.format.des3"),
    //数据加密参数密钥配置
    DES_PARAM("cgcg.format.des3.param"),
    //数据加密返回结果密钥配置
    DES_RESULT("cgcg.format.des3.result");

    private final String key;
    private Object property;

    FormatProperty(String key) {
        this.key = key;
    }

    public static void init() {
        final FormatProperty[] values = FormatProperty.values();
        for (FormatProperty value : values) {
            value.property = SpringContextHolder.getProperty(value.key);
        }
    }

    public String getString() {
        return property != null ? property.toString() : null;
    }

    public Boolean getBoolean() {
        return property != null ? Boolean.valueOf(property.toString()): null;
    }

    @SuppressWarnings("unchecked,unuse")
    public <T> T getProperty(Class<T> tClass) {
        return (T) this.property;
    }

    public static String des(FormatProperty format) {
        final Object property = format.getProperty();
        if (DES_ROOT.equals(format)) {
            return format.getString();
        }
        if (property == null) {
            return DES_ROOT.getString();
        }
        return null;
    }

    public static Class<?> getFormatClass() {
        final String className = CLASS_NAME.getString();
        Class<?> formatClass = Result.class;
        try {
            if (StringUtils.isNotBlank(className)) {
                formatClass = Class.forName(className);
            }
        } catch (Exception e) {
            log.warn("[{}={}]配置错误", CLASS_NAME.getKey(), className);
        }
        return formatClass;
    }
}
