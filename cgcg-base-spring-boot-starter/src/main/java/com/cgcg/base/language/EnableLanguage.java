package com.cgcg.base.language;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({CustomLocaleResolver.class})
public @interface EnableLanguage {
}
