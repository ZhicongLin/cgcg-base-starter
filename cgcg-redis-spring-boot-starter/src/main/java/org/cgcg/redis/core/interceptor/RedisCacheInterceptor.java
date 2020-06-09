package org.cgcg.redis.core.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.cgcg.redis.core.annotation.RedisCache;
import org.cgcg.redis.core.entity.RedisCacheHandle;
import org.cgcg.redis.core.entity.RedisCacheResult;
import org.cgcg.redis.core.enums.RedisExecuteType;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * Description:
 *
 * @author linzc
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本           修改人       修改日期         修改内容
 * 2020/6/5.1       linzc    2020/6/5           Create
 * </pre>
 * @date 2020/6/5
 */
@Slf4j
public class RedisCacheInterceptor extends AbstractRedisCacheInterceptor implements MethodInterceptor {

    public RedisCacheInterceptor(RedisTemplate<String, Object> redisTemplate, Environment environment) {
        super(redisTemplate, environment);
    }

    public Object invoke(MethodInvocation methodInvocation, RedisCache redisCacheAnnotation) throws Throwable {
        final RedisCacheHandle redisCacheHandle = getRedisCacheHandle(methodInvocation, redisCacheAnnotation);
        //方法执行前查询缓存，并判断是否需要执行方法
        RedisCacheResult cacheResult;
        if (redisCacheAnnotation.type().equals(RedisExecuteType.SELECT)) {
            cacheResult = RedisCacheExecutor.beforeMethodInvoke(redisTemplate, redisCacheHandle, methodInvocation);
        } else {
            cacheResult = RedisCacheModifyExecutor.beforeMethodInvoke(redisTemplate, redisCacheHandle, methodInvocation);
        }
        Object result = cacheResult.getResult();
        if (cacheResult.isInvoke()) {
            result = methodInvocation.proceed();
            //方法执行后操作缓存
            if (redisCacheAnnotation.type().equals(RedisExecuteType.SELECT)) {
                RedisCacheExecutor.afterMethodInvoke(redisTemplate, result, redisCacheHandle, methodInvocation);
            } else {
                RedisCacheModifyExecutor.afterMethodInvoke(redisTemplate, result, redisCacheHandle, methodInvocation);
            }
        }
        return result;
    }

}