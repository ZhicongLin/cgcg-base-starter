package com.cgcg.rest.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DynamicParam {
    String value() default "";

    boolean isUrl() default false;
}
