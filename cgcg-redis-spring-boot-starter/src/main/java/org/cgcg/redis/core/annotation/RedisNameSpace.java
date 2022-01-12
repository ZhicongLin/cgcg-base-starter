package org.cgcg.redis.core.annotation;

import org.cgcg.redis.core.enums.RedisTimeUnit;

import java.lang.annotation.*;

/**
 * Redis命名空间注解.
 *
 * @author zhicong.lin
 * @date 2019/6/21
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RedisNameSpace {

    String cache();

    String[] expire() default {};

    RedisTimeUnit unit() default RedisTimeUnit.NULL;

}
