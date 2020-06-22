package org.cgcg.redis.core.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.cgcg.redis.core.entity.RedisCacheResult;
import org.cgcg.redis.core.entity.RedisHitRate;
import org.cgcg.redis.core.entity.RedisMethodSignature;
import org.cgcg.redis.core.enums.RedisExecuteType;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.fastjson.JSON;

import io.lettuce.core.RedisConnectionException;
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

    /**
     * 在方法执行之前执行
     *
     * @param redisTemplate
     * @param signature
     * @throws Throwable
     */
    public static RedisCacheResult beforeMethodInvoke(RedisTemplate<String, Object> redisTemplate, RedisMethodSignature signature) {
        val redisCache = signature.getRedisCache();
        val cacheKey = signature.getKey();
        val builder = RedisCacheResult.builder().executeMethod(true);
        try {
            if (!connection) {
                throw new RedisConnectionException("Miss Redis Cache Server Connection");
            }

            val valueOperations = redisTemplate.opsForValue();
            val result = valueOperations.get(cacheKey);
            //SELECT， 有缓存，则不执行方法返回缓存结果，无缓存则执行方法
            if (result != null && StringUtils.isNotBlank(result.toString())) {
                RedisHitRate.addHitCount(cacheKey, redisTemplate);
                log.info("Hit Redis Cache [{}] Rate {}", cacheKey, RedisHitRate.getRate(cacheKey, redisTemplate));
                val object = JSON.parseObject(result.toString(), signature.getReturnType());
                builder.executeMethod(false).result(object);
            }
        } catch (Exception e) {
            log.warn("Miss Redis Cache Server Connection", e);
            connection = false;
        }
        return builder.build();
    }


    /**
     * 在方法执行结束后执行
     *
     * @param redisTemplate
     * @param result
     * @param signature
     * @throws Throwable
     */
    public static void afterMethodInvoke(RedisTemplate<String, Object> redisTemplate, Object result, RedisMethodSignature signature) {
        try {
            if (!connection) {
                throw new RedisConnectionException("Miss Redis Cache Server Connection");
            }
            val redisCache = signature.getRedisCache();

            if (!RedisExecuteType.SELECT.equals(redisCache.type())) {
                return;
            }
            cacheMethodResult(redisTemplate, result, redisCache, signature.getKey(), signature.getExpire());
        } catch (Exception e) {
            log.warn("Miss Redis Cache Server Connection", e);
            connection = false;
        }
    }

}