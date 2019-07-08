package com.cgcg.base.validate.annotation;

import java.lang.annotation.*;

/**
 * 数据校验注解.
 *
 * @author zhicong.lin
 * @date 2019/7/8
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Validate {

    Class<?> value();

    String method() default "";
}
