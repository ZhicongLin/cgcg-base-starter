package org.cgcg.redis.core.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.cgcg.redis.core.RedisHelper;
import org.cgcg.redis.core.entity.Callback;
import org.cgcg.redis.core.entity.RedisCacheMethod;
import org.cgcg.redis.core.entity.RedisCacheResult;
import org.cgcg.redis.core.entity.RedisTask;
import org.cgcg.redis.core.enums.RedisExecuteType;
import org.springframework.data.redis.core.RedisTemplate;

import com.cgcg.context.SpringContextHolder;

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

    private static final String KEY_TEMP = "%s::%s(%s)";

    /**
     * 在方法执行之前执行
     *
     * @param redisTemplate
     * @param rcm
     * @throws Throwable
     */
    public static RedisCacheResult beforeMethodInvoke(RedisTemplate<String, Object> redisTemplate, RedisCacheMethod rcm) {
        return RedisCacheResult.builder().executeMethod(true).build();
    }

    /**
     * 在方法执行结束后执行
     *
     * @param redisTemplate
     * @param result
     * @param rcm
     * @throws Throwable
     */
    public static void afterMethodInvoke(RedisTemplate<String, Object> redisTemplate, Object result,  RedisCacheMethod rcm) {
        val redisCache = rcm.getRedisCache();
        val cacheKey = getCacheKey(rcm, rcm.getMethod(), rcm.getArgs());
        if (RedisExecuteType.DELETE.equals(redisCache.type())) {
            //DELETE，删除缓存
            if (redisCache.lock()) {
                AsyncTemplate.async(() -> redisTemplate.delete(cacheKey), cacheKey);
            } else {
                redisTemplate.delete(cacheKey);
            }
        } else if (RedisExecuteType.FLUSH.equals(redisCache.type())) {
            //FLUSH, 清理所有想用cache的缓存
            flushCache(redisTemplate, rcm);
        } else {
            //UPDATE
            cacheMethodResult(redisTemplate, result, redisCache, cacheKey, rcm.getExpire());
        }
    }

    /**
     * 清空当前缓存的所有数据
     *
     * @auth zhicong.lin
     * @date 2019/6/26
     */
    private static void flushCache(RedisTemplate<String, Object> redisTemplate, RedisCacheMethod rcm) {

        val cache = rcm.getCache();
        if (StringUtils.isBlank(cache)) {
            log.error("Redis Cache FLUSH Error, @RedisCache.cache Value cannot be empty");
            return;
        }
        val keys = redisTemplate.keys(cache + "_CHN::*");
        if (keys != null && !keys.isEmpty()) {
            //清空缓存数据
            if (rcm.getRedisCache().lock()) {
                log.info("Redis Flush Caches {}", StringUtils.join(keys));
                AsyncTemplate.async(() -> redisTemplate.delete(keys), cache);
            } else {
                redisTemplate.delete(keys);
            }
        }
    }

    static class AsyncTemplate {

        /**
         * 异步执行
         *
         * @param callback
         */
        public static void async(Callback callback, String key) {
            val redisHelper = SpringContextHolder.getBean(RedisHelper.class);
            RedisTask.executeAsync(redisHelper, key, callback);
        }
    }
}