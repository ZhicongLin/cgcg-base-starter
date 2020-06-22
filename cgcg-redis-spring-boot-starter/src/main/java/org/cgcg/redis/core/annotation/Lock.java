package org.cgcg.redis.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Redis锁注解.
 *
 * @author zhicong.lin
 * @date 2019/6/20
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Lock {

    String key() default "";
    /**
     * 时间time 单位（ms)
     */
    int time() default 100;

    /**
     * true 执行结束后解锁
     * false 时间过期后解锁
     */
    boolean autoLock() default true;
}
