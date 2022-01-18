package org.cgcg.redis.core.annotation;

import org.cgcg.redis.core.mq.RedisMqPushFailCallback;

import java.lang.annotation.*;

/**
 * Redis消息生产者注解
 *
 * @author zhicong.lin
 * @date 2019/6/20
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Rmqp {
    //通道值
    String[] value();
    //推送失败重试次数 <=1不重试
    int retry() default 1;
    //回调方法
    Class<? extends RedisMqPushFailCallback> fallback() default RedisMqPushFailCallback.class;

}
