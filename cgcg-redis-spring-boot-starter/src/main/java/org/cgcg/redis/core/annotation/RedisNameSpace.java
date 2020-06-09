package org.cgcg.redis.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

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

    String expire() default "";

    TimeUnit unit() default TimeUnit.SECONDS;

}
