package org.cgcg.redis.core.penetrate;

import org.cgcg.redis.core.RedisManager;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 缓存穿透处理
 *
 * @author zhicong.lin
 * @date 2022/1/17
 */
public final class RedisPenetrate {

    public static final String PENETRATE_KEY = "redis-dangerous-keys";
    public static final String PENETRATE_VALUE = "method-null-value";

    /**
     * 缓存穿透的key
     *
     * @param key key
     */
    public static void setPenetrate(String key) {
        final int count = keyCount(key);
        final int currentValue = count + 1;
        getHashOps().put(PENETRATE_KEY, key, currentValue);
        //如果缓存查询为空，且执行方法后也为空，超过3次的话，则将缓存"method-null-value"字符串, 接下来1分钟内不会穿透到数据库
        if (currentValue >= 3) {
            getValueOps().set(key, PENETRATE_VALUE, 60L, TimeUnit.SECONDS);
        }
    }

    /**
     * 清理掉缓存不再穿透的KEY。
     *
     * @param keys keys
     */
    public static void clear(Set<Object> keys) {
        final ValueOperations<String, Object> valueOps = getValueOps();
        final HashOperations<String, Object, Object> hashOps = getHashOps();
        for (Object key : keys) {
            final Object value = valueOps.get(key.toString());
            if (value != null && value.equals(PENETRATE_VALUE)) {
                // 风险解除，清理掉key
                hashOps.delete(PENETRATE_KEY, key);
            }
        }
    }

    private static HashOperations<String, Object, Object> getHashOps() {
        return RedisManager.getRedisTemplate().opsForHash();
    }

    private static ValueOperations<String, Object> getValueOps() {
        return RedisManager.getRedisTemplate().opsForValue();
    }

    private static int keyCount(String key) {
        final Object val = getHashOps().get(PENETRATE_KEY, key);
        return val != null ? Integer.parseInt(val.toString()) : 0;
    }

}
