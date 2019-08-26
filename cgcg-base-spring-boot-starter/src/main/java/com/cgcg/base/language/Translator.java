package com.cgcg.base.language;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class Translator {

    private static MessageSource messageSource;

    public Translator(@Autowired MessageSource messageSource) {
        Translator.messageSource = messageSource;
    }

    public static String toLocale(String msg) {
        if (StringUtils.isBlank(msg)) {
            return null;
        }
        final Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(msg, null, locale);
    }

    public static String toLocale(String code, String defaultMsg) {
        final String result = Translator.toLocale(code);
        return result != null && (defaultMsg == null || !result.equals(code)) ? result : defaultMsg;
    }

}