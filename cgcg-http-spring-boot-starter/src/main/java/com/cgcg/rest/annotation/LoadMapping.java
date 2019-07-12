package com.cgcg.rest.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping(method = RequestMethod.GET)
public @interface LoadMapping {

    @AliasFor("name")
    String[] value() default "";

    @AliasFor("value")
    String[] name() default "";

    /**
     * 是否下载.
     *
     * @Param: []
     * @Return: boolean
     * @Author: ZhiCong Lin
     * @Date: 2018/8/14 17:26
     */
    boolean down() default false;
}
