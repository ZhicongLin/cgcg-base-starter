package org.cgcg.redis.core.interceptor;

import java.util.Set;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.cgcg.redis.core.RedisHelper;
import org.cgcg.redis.core.entity.Callback;
import org.cgcg.redis.core.entity.RedisCacheHandle;
import org.cgcg.redis.core.entity.RedisCacheResult;
import org.cgcg.redis.core.entity.RedisTask;
import org.cgcg.redis.core.enums.RedisExecuteType;
import org.springframework.data.redis.core.RedisTemplate;

import com.cgcg.context.SpringContextHolder;

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
public class RedisCacheModifyExecutor extends AbstractRedisCacheExecutor {

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
        redisCacheResult.setInvoke(true);
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
        if (RedisExecuteType.DELETE.equals(redisCacheHandle.getType())) {
            //DELETE，删除缓存
            if (redisCacheHandle.isLock()) {
                AsyncTemplate.async(() -> redisTemplate.delete(cacheKey), cacheKey);
            } else {
                redisTemplate.delete(cacheKey);
            }
        } else if (RedisExecuteType.FLUSH.equals(redisCacheHandle.getType())) {
            //FLUSH, 清理所有想用cache的缓存
            flushCache(redisTemplate, redisCacheHandle);
        } else {
            //UPDATE
            cacheMethodResult(redisTemplate, result, redisCacheHandle, cacheKey);
        }
    }

    /**
     * 清空当前缓存的所有数据
     *
     * @auth zhicong.lin
     * @date 2019/6/26
     */
    private static void flushCache(RedisTemplate<String, Object> redisTemplate, RedisCacheHandle redisCacheHandle) {

        final String cache = redisCacheHandle.getCache();
        if (StringUtils.isBlank(cache)) {
            log.error("Redis Cache FLUSH Error, @RedisCache.cache Value cannot be empty");
            return;
        }
        final Set<String> keys = redisTemplate.keys(cache + "_CHN::*");
        if (keys != null && !keys.isEmpty()) {
            //清空缓存数据
            if (redisCacheHandle.isLock()) {
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
            final RedisHelper redisHelper = SpringContextHolder.getBean(RedisHelper.class);
            RedisTask.executeAsync(redisHelper, key, callback);
        }
    }
}