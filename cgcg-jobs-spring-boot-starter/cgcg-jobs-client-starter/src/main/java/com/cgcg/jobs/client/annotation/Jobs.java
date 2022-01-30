package com.cgcg.jobs.client.annotation;

import java.lang.annotation.*;

/**
 * @author zhicong.lin
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Jobs {
    String value() default "";
}
