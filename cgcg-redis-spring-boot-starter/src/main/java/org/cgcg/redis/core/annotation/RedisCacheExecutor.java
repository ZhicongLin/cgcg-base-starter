package org.cgcg.redis.core.annotation;

import java.lang.reflect.Method;
import java.util.Set;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.cgcg.redis.core.entity.Callback;
import org.cgcg.redis.core.entity.RedisCacheHandle;
import org.cgcg.redis.core.entity.RedisCacheResult;
import org.cgcg.redis.core.RedisHelper;
import org.cgcg.redis.core.entity.RedisHitRate;
import org.cgcg.redis.core.entity.RedisTask;
import org.cgcg.redis.core.enums.RedisExecuteType;
import org.cgcg.redis.core.util.SpelUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.alibaba.fastjson.JSON;
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
public class RedisCacheExecutor {

    private static final String KEY_TEMP = "%s::%s(%s)";

    /**
     * 在方法执行之前执行
     *
     * @param redisTemplate
     * @param redisCacheHandle
     * @param methodInvocation
     * @throws Throwable
     */
    public static RedisCacheResult beforeMethodInvoke(RedisTemplate<String, Object> redisTemplate, RedisCacheHandle redisCacheHandle, MethodInvocation methodInvocation) throws Throwable {
        final RedisCacheResult redisCacheResult = new RedisCacheResult();
        //UPDATE， 则无需查询缓存数据库
        final RedisExecuteType type = redisCacheHandle.getType();
        if (RedisExecuteType.UPDATE.equals(type)) {
            redisCacheResult.setInvoke(true);
            return redisCacheResult;
        }
        final String cacheKey = getCacheKey(redisCacheHandle, methodInvocation.getMethod(), methodInvocation.getArguments());
        final ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        //DELETE\FLUSH类型，设置方法可执行
        if (RedisExecuteType.DELETE.equals(type) || RedisExecuteType.FLUSH.equals(type)) {
            redisCacheResult.setInvoke(true);
            return redisCacheResult;
        }
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
    public static void afterMethodInvoke(RedisTemplate<String, Object> redisTemplate, Object result, RedisCacheHandle redisCacheHandle, MethodInvocation methodInvocation) throws Throwable {

        final String cacheKey = getCacheKey(redisCacheHandle, methodInvocation.getMethod(), methodInvocation.getArguments());
        //DELETE，删除缓存
        if (RedisExecuteType.DELETE.equals(redisCacheHandle.getType())) {
            if (redisCacheHandle.isLock()) {
                AsyncTemplate.async(() -> redisTemplate.delete(cacheKey), cacheKey);
            } else {
                redisTemplate.delete(cacheKey);
            }
            return;
        }

        //FLUSH, 清理所有想用cache的缓存
        if (RedisExecuteType.FLUSH.equals(redisCacheHandle.getType())) {
            flushCache(redisTemplate, redisCacheHandle);
            return;
        }

        final ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        //方法执行结果为null
        if (result == null) {
            //原先有缓存数据时，则删除原先的数据
            if (Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey))) {
                redisTemplate.delete(cacheKey);
            }
            return;
        }

        //更新，刷新，查询，缓存方法执行结果
        if (redisCacheHandle.isLock()) {
            //加锁一般放在更新操作
            AsyncTemplate.async(() -> valueOperations.set(cacheKey, JSON.toJSONString(result), redisCacheHandle.getExpire(), redisCacheHandle.getTimeUnit()), redisCacheHandle.getCache());
        } else {
            valueOperations.set(cacheKey, JSON.toJSONString(result), redisCacheHandle.getExpire(), redisCacheHandle.getTimeUnit());
        }
        RedisHitRate.addMissCount(cacheKey, redisTemplate);
        log.info("Redis Cache [{}] Success, Rate {}.", cacheKey, RedisHitRate.getRate(cacheKey, redisTemplate));
    }

    /**
     * 获取缓存key
     *
     * @param redisCacheHandle
     * @param method
     * @param args
     * @return
     */
    private static String getCacheKey(RedisCacheHandle redisCacheHandle, Method method, Object[] args) {
        final String key = redisCacheHandle.getKey();
        String cacheKey = key;
        if (StringUtils.isBlank(key)) {
            cacheKey = JSON.toJSONString(args);
        } else if (key.contains("#")) {
            cacheKey = SpelUtils.parse(method.getDeclaringClass().getName(), key, method, args);
        }
        return redisCacheHandle.getCache() + "_CHN::" + cacheKey;
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