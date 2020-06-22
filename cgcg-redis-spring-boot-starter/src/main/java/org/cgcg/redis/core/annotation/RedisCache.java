package org.cgcg.redis.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import org.cgcg.redis.core.enums.RedisExecuteType;

/**
 * Redis缓存注解.
 *
 * @author zhicong.lin
 * @date 2019/6/20
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RedisCache {
    String cache() default "";

    String key() default "";

    String expire() default "";

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    boolean lock() default false;

    RedisExecuteType type() default RedisExecuteType.SELECT;

}
