package org.cgcg.redis.core.interceptor;

import org.aopalliance.intercept.MethodInvocation;
import org.cgcg.redis.core.entity.RedisCacheHandle;
import org.cgcg.redis.core.entity.RedisCacheResult;
import org.cgcg.redis.core.entity.RedisHitRate;
import org.cgcg.redis.core.enums.RedisExecuteType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;

/**
 * Description: 缓存执行器
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
public class RedisCacheExecutor extends AbstractRedisCacheExecutor {

    private static final String KEY_TEMP = "%s::%s(%s)";

    /**
     * 在方法执行之前执行
     *
     * @param redisTemplate
     * @param redisCacheHandle
     * @param methodInvocation
     * @throws Throwable
     */
    public static RedisCacheResult beforeMethodInvoke(RedisTemplate<String, Object> redisTemplate, RedisCacheHandle redisCacheHandle, MethodInvocation methodInvocation) {
        final RedisCacheResult redisCacheResult = new RedisCacheResult();
        final String cacheKey = getCacheKey(redisCacheHandle, methodInvocation.getMethod(), methodInvocation.getArguments());
        final ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        final Object result = valueOperations.get(cacheKey);
        //SELECT， 有缓存，则不执行方法返回缓存结果，无缓存则执行方法
        if (result == null) {
            redisCacheResult.setInvoke(true);
        } else {
            RedisHitRate.addHitCount(cacheKey, redisTemplate);
            log.info("Hit Redis Cache [{}], Rate {} .", cacheKey, RedisHitRate.getRate(cacheKey, redisTemplate));
            final Class<?> returnType = methodInvocation.getMethod().getReturnType();
            redisCacheResult.setResult(JSON.parseObject(result.toString(), returnType));
        }
        return redisCacheResult;
    }

    /**
     * 在方法执行结束后执行
     *
     * @param redisTemplate
     * @param result
     * @param redisCacheHandle
     * @param methodInvocation
     * @throws Throwable
     */
    public static void afterMethodInvoke(RedisTemplate<String, Object> redisTemplate, Object result, RedisCacheHandle redisCacheHandle, MethodInvocation methodInvocation) {

        final String cacheKey = getCacheKey(redisCacheHandle, methodInvocation.getMethod(), methodInvocation.getArguments());

        if (!RedisExecuteType.SELECT.equals(redisCacheHandle.getType())) {
            return;
        }

        cacheMethodResult(redisTemplate, result, redisCacheHandle, cacheKey);
    }

}