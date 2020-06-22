package org.cgcg.redis.core.annotation;

import java.lang.reflect.Method;

import javax.annotation.Resource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.cgcg.redis.core.entity.RedisMethodSignature;
import org.cgcg.redis.core.interceptor.AbstractRedisCacheExecutor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.Setter;
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
@Setter
@Aspect
@Component
public class RedisCacheAspect implements EnvironmentAware {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private Environment environment;

    @Around(value = "@annotation(redisCache)")
    public Object round(ProceedingJoinPoint pjp, RedisCache redisCache) throws Throwable {
        final Object[] arguments = pjp.getArgs();
        if (!AbstractRedisCacheExecutor.connection) {
            log.warn("Miss Redis Cache Server Connection");
            return pjp.proceed(arguments);
        }
        //获取缓存方法签名参数
        final Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        final RedisMethodSignature signature = new RedisMethodSignature(method, arguments, this.environment, redisCache);
        //调度器执行缓存操作
        final RedisCacheDispatcher dispatcher = new RedisCacheDispatcher(signature, this.redisTemplate) {
            @Override
            public Object execute() throws Throwable {
                return pjp.proceed(arguments);
            }
        };
        return dispatcher.getResult();
    }

}