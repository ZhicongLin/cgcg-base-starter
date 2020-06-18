package org.cgcg.redis.core.annotation;

import javax.annotation.Resource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.cgcg.redis.core.RedisHelper;
import org.cgcg.redis.core.exception.RedisLockException;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.val;

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
@Order(-1)
@Aspect
@Component
public class RedisLockAspect {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private RedisHelper redisHelper;

    @Around(value = "@annotation(redisLock)")
    public Object round(ProceedingJoinPoint proceedingJoinPoint, RedisLock redisLock) throws Throwable {
        val key = redisLock.key();
        try {
            if (redisHelper.lock(key, redisLock.time())) {
                return proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
            }
            throw new RedisLockException();
        } finally {
            if (redisLock.unlock()) {
                redisHelper.delete(key);
            }
        }
    }

}