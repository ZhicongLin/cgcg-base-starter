package org.cgcg.redis.core.annotation;


import java.lang.annotation.*;

/**
 * Redis消费者注解
 *
 * @author zhicong.lin
 * @date 2019/6/20
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Rmqc {
    String value();

    //消息内容处理失败重试次数 0不重试
    int retry() default 0;
}
