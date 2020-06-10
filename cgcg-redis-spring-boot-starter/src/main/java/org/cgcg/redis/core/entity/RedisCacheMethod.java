package org.cgcg.redis.core.entity;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.cgcg.redis.core.annotation.RedisCache;
import org.cgcg.redis.core.annotation.RedisNameSpace;
import org.cgcg.redis.core.util.SpelUtils;
import org.springframework.core.env.Environment;

import lombok.Data;

/**
 * Description: 缓存方法对象
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
@Data
public class RedisCacheMethod {

    private Method method;
    private Object[] args;
    private Class<?> returnType;
    private Class<?> beanType;
    private long expire;
    private String cache;
    private TimeUnit timeUnit;
    private String key;
    private RedisCache redisCache;

    private RedisCacheMethod(ProceedingJoinPoint joinPoint) {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        this.method = signature.getMethod();
        this.returnType = signature.getReturnType();
        this.beanType = this.method.getDeclaringClass();
        this.args = joinPoint.getArgs();
    }

    public RedisCacheMethod(MethodInvocation invocation) {
        this.method = invocation.getMethod();
        this.returnType = this.method.getReturnType();
        this.beanType = invocation.getMethod().getDeclaringClass();
        this.args = invocation.getArguments();
    }

    public static RedisCacheMethod build(ProceedingJoinPoint joinPoint, RedisCache redisCache, Environment environment) {
        final RedisCacheMethod cacheMethod = new RedisCacheMethod(joinPoint);
        addNameSpace(redisCache, environment, cacheMethod);
        return cacheMethod;
    }

    public static RedisCacheMethod build(MethodInvocation methodInvocation, RedisCache redisCache, Environment environment) {
        final RedisCacheMethod cacheMethod = new RedisCacheMethod(methodInvocation);
        addNameSpace(redisCache, environment, cacheMethod);
        return cacheMethod;
    }

    private static void addNameSpace(RedisCache redisCache, Environment environment, RedisCacheMethod cacheMethod) {
        cacheMethod.key = redisCache.key();
        cacheMethod.setRedisCache(redisCache);
        final RedisNameSpace rns = cacheMethod.beanType.getAnnotation(RedisNameSpace.class);
        if (rns != null) {
            cacheMethod.expire = SpelUtils.getExpireTime(rns.expire(), environment);
            cacheMethod.cache = rns.cache();
            cacheMethod.timeUnit = rns.unit();
        }
        if (StringUtils.isNoneBlank(redisCache.cache())) {
            cacheMethod.cache = redisCache.cache();
        }
        if (StringUtils.isNoneBlank(redisCache.expire())) {
            cacheMethod.expire = SpelUtils.getExpireTime(redisCache.expire(), environment);
            cacheMethod.timeUnit = redisCache.timeUnit();
        }
    }

}