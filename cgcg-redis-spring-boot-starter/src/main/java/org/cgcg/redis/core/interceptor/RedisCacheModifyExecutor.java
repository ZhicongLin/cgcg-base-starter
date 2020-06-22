package org.cgcg.redis.core.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.cgcg.redis.core.entity.RedisCacheResult;
import org.cgcg.redis.core.entity.RedisMethodSignature;
import org.cgcg.redis.core.enums.RedisExecuteType;
import org.springframework.data.redis.core.RedisTemplate;

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
public class RedisCacheModifyExecutor extends AbstractRedisCacheExecutor {

    /**
     * 在方法执行之前执行
     *
     * @param redisTemplate
     * @param signature
     * @throws Throwable
     */
    public static RedisCacheResult beforeMethodInvoke(RedisTemplate<String, Object> redisTemplate, RedisMethodSignature signature) {
        if (!connection) {
            log.warn("Miss Redis Cache Server Connection");
        }
        return RedisCacheResult.builder().executeMethod(true).build();
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
            val cacheKey = signature.getKey();
            if (RedisExecuteType.DELETE.equals(redisCache.type())) {
                //DELETE，删除缓存
                if (redisCache.lock()) {
                    AsyncTemplate.async(() -> redisTemplate.delete(cacheKey), cacheKey);
                } else {
                    redisTemplate.delete(cacheKey);
                }
            } else if (RedisExecuteType.FLUSH.equals(redisCache.type())) {
                //FLUSH, 清理所有想用cache的缓存
                flushCache(redisTemplate, signature);
            } else {
                //UPDATE
                cacheMethodResult(redisTemplate, result, redisCache, signature.getKey(), signature.getExpire());
            }
        } catch (Exception e) {
            log.warn("Miss Redis Cache Server Connection", e);
            connection = false;
        }
    }

    /**
     * 清空当前缓存的所有数据
     *
     * @auth zhicong.lin
     * @date 2019/6/26
     */
    private static void flushCache(RedisTemplate<String, Object> redisTemplate, RedisMethodSignature signature) {

        val cache = signature.getCache();
        if (StringUtils.isBlank(cache)) {
            log.error("Redis Cache FLUSH Error, @RedisCache.cache Value cannot be empty");
            return;
        }
        val keys = redisTemplate.keys("chk::" + cache + "::*");
        if (keys != null && !keys.isEmpty()) {
            //清空缓存数据
            if (signature.getRedisCache().lock()) {
                log.info("Redis Flush Caches {}", StringUtils.join(keys));
                AsyncTemplate.async(() -> redisTemplate.delete(keys), cache);
            } else {
                redisTemplate.delete(keys);
            }
        }
    }

}