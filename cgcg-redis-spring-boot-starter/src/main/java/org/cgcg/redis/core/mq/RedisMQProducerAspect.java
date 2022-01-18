package org.cgcg.redis.core.mq;

import com.cgcg.context.util.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.cgcg.redis.core.RedisManager;
import org.cgcg.redis.core.annotation.Rmqp;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Redis缓存处理Aop.
 *
 * @author zhicong.lin
 * @date 2019/6/20
 */
@Order(-2)
@Aspect
@Component
@Slf4j
public class RedisMQProducerAspect {

    @Around(value = "@annotation(rmqp)")
    public Object processor(ProceedingJoinPoint joinPoint, Rmqp rmqp) throws Throwable {
        Object result = joinPoint.proceed();
        final Class<? extends RedisMqPushFailCallback> back = rmqp.back();
        final String[] channel = rmqp.value();
        for (String cn : channel) {
            if (!back.equals(RedisMqPushFailCallback.class)) {
                RedisMqPublisher.send(cn, result, rmqp.retry());
            } else {
                RedisMqPublisher.send(cn, result, rmqp.retry(), back.newInstance());
            }
        }
        return result;
    }

}
