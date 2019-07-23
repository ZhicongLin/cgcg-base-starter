package com.cgcg.rest.annotation;

import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.*;

/**
 * 下载的注解.
 *
 * @Author: ZhiCong.Lin
 * @Create: 2018-08-14 17:19
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoadMapping {

    String[] value() default "";

    RequestMethod[] method() default RequestMethod.GET;
}
