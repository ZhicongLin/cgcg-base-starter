package com.cgcg.rest.annotation;

import com.cgcg.rest.filter.RestFilter;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MappingFilter {

    Class<? extends RestFilter> name();
}
