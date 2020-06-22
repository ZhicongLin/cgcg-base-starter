package org.cgcg.redis.core.annotation;

import java.lang.reflect.Method;

import javax.annotation.Resource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.cgcg.redis.core.entity.RedisCacheMethod;
import org.cgcg.redis.core.entity.RedisCacheResult;
import org.cgcg.redis.core.enums.RedisExecuteType;
import org.cgcg.redis.core.interceptor.AbstractRedisCacheExecutor;
import org.cgcg.redis.core.interceptor.RedisCacheExecutor;
import org.cgcg.redis.core.interceptor.RedisCacheModifyExecutor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Setter
@Aspect
@Component
public class RedisCacheAspect implements EnvironmentAware {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private Environment environment;

    @Around(value = "@annotation(redisCache)")
    public Object round(ProceedingJoinPoint pjp, RedisCache redisCache) throws Throwable {
        if (!AbstractRedisCacheExecutor.connection) {
            log.warn("Miss Redis Cache Server Connection");
            return pjp.proceed(pjp.getArgs());
        }
        final Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        final RedisCacheMethod rcm = new RedisCacheMethod(method, pjp.getArgs(), environment, redisCache);

        //方法执行前查询缓存，并判断是否需要执行方法
        RedisCacheResult cacheResult;
        if (redisCache.type().equals(RedisExecuteType.SELECT)) {
            cacheResult = RedisCacheExecutor.beforeMethodInvoke(redisTemplate, rcm);
        } else {
            cacheResult = RedisCacheModifyExecutor.beforeMethodInvoke(redisTemplate, rcm);
        }
        if (!cacheResult.isExecuteMethod()) {
            return cacheResult.getResult();
        }
        val result = pjp.proceed(rcm.getArgs());
        //方法执行后操作缓存
        if (redisCache.type().equals(RedisExecuteType.SELECT)) {
            RedisCacheExecutor.afterMethodInvoke(redisTemplate, result, rcm);
        } else {
            RedisCacheModifyExecutor.afterMethodInvoke(redisTemplate, result, rcm);
        }
        return result;
    }

}