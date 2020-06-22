package org.cgcg.redis.core.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.cgcg.redis.core.entity.RedisLock;
import org.cgcg.redis.core.interceptor.AbstractRedisCacheExecutor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Description: 缓存aop
 *
 * @author linzc
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本           修改人       修改日期         修改内容
 * 2020/6/10.1       linzc    2020/6/10           Create
 * </pre>
 * @date 2020/6/10
 */
@Slf4j
@Order(-1)
@Aspect
@Component
public class RedisLockAspect {

    @Around(value = "@annotation(lockAnnotation)")
    public Object round(ProceedingJoinPoint pjp, Lock lockAnnotation) throws Throwable {
        if (!AbstractRedisCacheExecutor.connection) {
            log.warn("Miss Redis Cache Server Connection");
            return pjp.proceed(pjp.getArgs());
        }
        final String key = lockAnnotation.key();
        final RedisLock redisLock = RedisLock.lockHear(key, lockAnnotation.time());
        final Object proceed = pjp.proceed(pjp.getArgs());
        if (lockAnnotation.autoLock()) {
            redisLock.unlock();
        }
        return proceed;
    }

}