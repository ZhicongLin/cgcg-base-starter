package org.cgcg.redis.core.annotation;

import org.cgcg.redis.core.enums.LockUnit;
import org.cgcg.redis.core.enums.RedisEnum;
import org.cgcg.redis.core.enums.RedisTimeUnit;

import java.lang.annotation.*;

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

    String[] expire() default {};

    RedisTimeUnit timeUnit() default RedisTimeUnit.NULL;

    LockUnit lock() default LockUnit.NULL;

    RedisEnum type() default RedisEnum.NULL;

    String condition() default "";
}
