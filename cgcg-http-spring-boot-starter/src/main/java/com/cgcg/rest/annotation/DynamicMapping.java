package com.cgcg.rest.annotation;

import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.*;

/**
 * 动态URL映射.
 *
 * @Author: ZhiCong.Lin
 * @Create: 2018-08-16 10:47
 */
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DynamicMapping {

    String[] value() default {};

    RequestMethod[] method() default RequestMethod.GET;
}
