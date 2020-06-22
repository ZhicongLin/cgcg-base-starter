package org.cgcg.redis.core.interceptor;

import org.cgcg.redis.core.RedisHelper;
import org.cgcg.redis.core.annotation.RedisCache;
import org.cgcg.redis.core.entity.Callback;
import org.cgcg.redis.core.entity.RedisHitRate;
import org.cgcg.redis.core.entity.RedisTask;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.alibaba.fastjson.JSON;
import com.cgcg.context.SpringContextHolder;

import lombok.extern.slf4j.Slf4j;
import lombok.var;

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
public class AbstractRedisCacheExecutor {

    public static boolean connection = true;

    protected static void cacheMethodResult(RedisTemplate<String, Object> redisTemplate, Object result,
                                            RedisCache redisCache, String cacheKey, long expire) {

        if (!connection) {
            log.warn("Miss Redis Cache Server Connection");
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
        if (redisCache.lock()) {
            //加锁一般放在更新操作
            AsyncTemplate.async(() -> setValue(result, redisCache, cacheKey, valueOperations, expire), redisCache.cache());
        } else {
            setValue(result, redisCache, cacheKey, valueOperations, expire);
        }
        RedisHitRate.addMissCount(cacheKey, redisTemplate);
        log.info("Redis Cache [{}] Success Rate {}", cacheKey, RedisHitRate.getRate(cacheKey, redisTemplate));

    }

    protected static void setValue(Object result, RedisCache redisCache, String cacheKey, ValueOperations<String, Object> valueOperations, long expire) {
        if (expire <= 0) {
            log.warn("When the redis expire time is less than or equal to zero, the system defaults to two hours");
            valueOperations.set(cacheKey, JSON.toJSONString(result), RedisHelper.DEFAULT_EXPIRE, RedisHelper.DEFAULT_TIME_UNIT);
        } else {
            valueOperations.set(cacheKey, JSON.toJSONString(result), expire, redisCache.timeUnit());
        }
    }

    static class AsyncTemplate {

        /**
         * 异步执行
         *
         * @param callback
         */
        public static void async(Callback callback, String key) {
            var redisHelper = SpringContextHolder.getBean(RedisHelper.class);
            RedisTask.executeAsync(redisHelper, key, callback);
        }
    }
}