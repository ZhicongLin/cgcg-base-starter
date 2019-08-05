package com.cgcg.base.language;

import com.cgcg.base.core.context.SpringContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

public class Translator {

    private static ResourceBundleMessageSource messageSource;

    public static String toLocale(String msg) {
        if (StringUtils.isBlank(msg)) {
            return null;
        }
        if (messageSource == null) {
            try {
                messageSource = SpringContextHolder.getBean(ResourceBundleMessageSource.class);
            } catch (NoSuchBeanDefinitionException e) {
                return msg;
            }
        }

        final Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(msg, null, locale);
    }

    public static String toLocale(String code, String defaultMsg) {
        final String result = Translator.toLocale(code);
        return result != null && (defaultMsg == null || !result.equals(code)) ? result : defaultMsg;
    }

}