package org.cgcg.redis.core.entity;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Description: Redis缓存命中率
 *
 * @author linzc
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本           修改人       修改日期         修改内容
 * 2020/6/8.1       linzc    2020/6/8           Create
 * </pre>
 * @date 2020/6/8
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RedisHitRate {

    private static final String HIT_KEY = "REDIS_CACHE_KEY_HIT_MAP";

    private static final String MISS_KEY = "REDIS_CACHE_KEY_MISS_MAP";

    /**
     * 计算缓存命中率
     * @param key
     * @param redisTemplate
     * @return
     */
    public static double getRate(String key, RedisTemplate<String, Object> redisTemplate) {
        final HashOperations<String, Object, Object> ops = redisTemplate.opsForHash();
        final Object hitCount = ops.get(HIT_KEY, key);
        final Object missCount = ops.get(MISS_KEY, key);
        if (hitCount != null && missCount != null) {
            final double longHit = Double.parseDouble(hitCount.toString());
            double totalCount = longHit + Long.parseLong(missCount.toString());
            return longHit / totalCount;
        }
        return 0.0;
    }

    /**
     * 添加未命中次数
     * @param key
     * @param redisTemplate
     */
    public static void addMissCount(String key, RedisTemplate<String, Object> redisTemplate) {
        add(key, redisTemplate, MISS_KEY);
    }

    /**
     * 添加命中次数
     * @param key
     * @param redisTemplate
     */
    public static void addHitCount(String key, RedisTemplate<String, Object> redisTemplate) {
        add(key, redisTemplate, HIT_KEY);
    }

    private static void add(String key, RedisTemplate<String, Object> redisTemplate, String missKey) {
        final HashOperations<String, Object, Object> ops = redisTemplate.opsForHash();
        final Object count = ops.get(missKey, key);
        if (count == null) {
            ops.put(missKey, key, 1);
        } else {
            ops.put(missKey, key, Long.parseLong(count.toString()) + 1);
        }
    }


}