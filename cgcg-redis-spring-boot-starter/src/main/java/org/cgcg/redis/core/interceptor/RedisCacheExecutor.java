package org.cgcg.redis.core.interceptor;

import org.cgcg.redis.core.entity.RedisCacheMethod;
import org.cgcg.redis.core.entity.RedisCacheResult;
import org.cgcg.redis.core.entity.RedisHitRate;
import org.cgcg.redis.core.enums.RedisExecuteType;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

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
     * @param rcm
     * @throws Throwable
     */
    public static RedisCacheResult beforeMethodInvoke(RedisTemplate<String, Object> redisTemplate, RedisCacheMethod rcm) {
        val redisCache = rcm.getRedisCache();
        val cacheKey = getCacheKey(rcm, rcm.getMethod(), rcm.getArgs());
        val valueOperations = redisTemplate.opsForValue();
        val result = valueOperations.get(cacheKey);
        //SELECT， 有缓存，则不执行方法返回缓存结果，无缓存则执行方法
        val builder = RedisCacheResult.builder();
        if (result == null) {
            return builder.executeMethod(true).build();
        }
        RedisHitRate.addHitCount(cacheKey, redisTemplate);
        log.info("Hit Redis Cache [{}], Rate {} .", cacheKey, RedisHitRate.getRate(cacheKey, redisTemplate));

        val object = JSON.parseObject(result.toString(), rcm.getReturnType());
        return builder.executeMethod(false).result(object).build();
    }


    /**
     * 在方法执行结束后执行
     *
     * @param redisTemplate
     * @param result
     * @param rcm
     * @throws Throwable
     */
    public static void afterMethodInvoke(RedisTemplate<String, Object> redisTemplate, Object result, RedisCacheMethod rcm) {
        val redisCache = rcm.getRedisCache();
        val cacheKey = getCacheKey(rcm, rcm.getMethod(), rcm.getArgs());

        if (!RedisExecuteType.SELECT.equals(redisCache.type())) {
            return;
        }

        cacheMethodResult(redisTemplate, result, redisCache, cacheKey, rcm.getExpire());
    }

}