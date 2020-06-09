package org.cgcg.redis.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.cgcg.redis.core.entity.RedisLock;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 通用Redis帮助类
 *
 * @auth zhicong.lin
 * @date 2019/6/21
 */
@Slf4j
@Component
public class RedisHelper {

    private static final String LOCK_PREFIX = "cgcg_redis";
    private static final String DEFAULT_CACHE_NAME = "DEFAULT_CG_NAMESPACES";
    private static final Long DEFAULT_EXPIRE = 2L;
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.HOURS;

    private final Map<String, RedisLock> locks = new HashMap<>();

    /**
     * 最终加强分布式锁
     *
     * @param key key值
     * @return 是否获取到
     */
    public boolean lock(String key) {
        return lock(key, 100);
    }
    /**
     * 最终加强分布式锁
     *
     * @param key key值
     * @return 是否获取到
     */
    public boolean lock(String key, int timeout) {
        final String lockKey = LOCK_PREFIX + "_" + key;
        final RedisLock redisLock = new RedisLock(lockKey, timeout);
        final boolean lock = redisLock.lock();
        if (lock) {
            locks.put(lockKey, redisLock);
        }
        return lock;
    }

    /**
     * 删除锁
     *
     * @param key
     */
    public void delete(String key) {
        final String lockKey = LOCK_PREFIX + "_" + key;
        final RedisLock redisLock = locks.get(lockKey);
        if (redisLock != null) {
            redisLock.unlock();
        }
    }

    /**
     * Hash
     *
     * @auth zhicong.lin
     * @date 2019/6/21
     */
    public void hset(String name, Object key, Object value, long time, TimeUnit unit) {
        final HashOperations<String, Object, Object> hashOperations = RedisManager.getRedisTemplate().opsForHash();
        hashOperations.put(name, key, value);
        if (time > 0) {
            RedisManager.getRedisTemplate().expire(name, time, unit);
        }
    }

    public void hset(String name, Object key, Object value) {
        hset(name, key, value, DEFAULT_EXPIRE, DEFAULT_TIME_UNIT);
    }

    public void hset(Object key, Object value) {
        hset(DEFAULT_CACHE_NAME, key, value);
    }

    public Object hget(String name, Object key) {
        final HashOperations<String, Object, Object> hashOperations = RedisManager.getRedisTemplate().opsForHash();
        return hashOperations.get(name, key);
    }

    public List<Object> hgetList(String name) {
        final HashOperations<String, Object, Object> hashOperations = RedisManager.getRedisTemplate().opsForHash();
        return hashOperations.values(name);
    }

    public Object hget(Object key) {
        return hget(DEFAULT_CACHE_NAME, key);
    }

    /**
     * kv-set
     *
     * @auth zhicong.lin
     * @date 2019/6/21
     */
    public void set(String key, Object value, long time, TimeUnit unit) {
        final ValueOperations<String, Object> opsForValue = RedisManager.getRedisTemplate().opsForValue();
        opsForValue.set(key, value, time, unit);
    }

    public void set(String key, Object value) {
        set(key, value, DEFAULT_EXPIRE, DEFAULT_TIME_UNIT);
    }

    public Object get(String key) {
        return RedisManager.getRedisTemplate().opsForValue().get(key);
    }

    public void rpush(String key, Object value) {
        RedisManager.getRedisTemplate().opsForList().rightPushAll(key, value);
        RedisManager.getRedisTemplate().expire(key, DEFAULT_EXPIRE, DEFAULT_TIME_UNIT);
    }

    public Object lpop(String key) {
        return RedisManager.getRedisTemplate().opsForList().leftPop(key);
    }

    public List<Object> list(String key) {
        return list(key, 0, -1);
    }

    public List<Object> list(String key, long start, long end) {
        return RedisManager.getRedisTemplate().opsForList().range(key, start, end);
    }

    public void del(String... key) {
        RedisManager.getRedisTemplate().delete(Arrays.asList(key));
    }

    public void del(Collection<String> keys) {
        RedisManager.getRedisTemplate().delete(keys);
    }

    public Set<String> keys(String name) {
        return RedisManager.getRedisTemplate().keys(name + "*");
    }

    public void remove(String name, Object... key) {
        RedisManager.getRedisTemplate().opsForHash().delete(name, key);
    }

    public void expire(String key, long time, TimeUnit unit) {
        RedisManager.getRedisTemplate().expire(key, time, unit);
    }

}
