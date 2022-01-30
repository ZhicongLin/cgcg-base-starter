package org.cgcg.redis.core.annotation;

import java.lang.annotation.*;

/**
 * Redis消息队列注解.
 *
 * @author zhicong.lin
 * @date 2019/6/20
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RmqListener {

}
