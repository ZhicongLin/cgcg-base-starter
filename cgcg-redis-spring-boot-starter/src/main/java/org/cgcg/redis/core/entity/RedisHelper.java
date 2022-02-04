package org.cgcg.redis.core.entity;

import lombok.extern.slf4j.Slf4j;
import org.cgcg.redis.core.RedisManager;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 通用Redis帮助类
 *
 * @author zhicong.lin
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
     * @author zhicong.lin
     * @date 2019/6/21
     */
    public void hset(String name, Object key, Object value, long time, TimeUnit unit) {
        final HashOperations<String, Object, Object> hashOperations = RedisManager.getRedisTemplate().opsForHash();
        hashOperations.put(name, key, value);
        if (time > 0) {
            RedisManager.getRedisTemplate().expire(name, time, unit);
        }
    }

    /**
     * 设置hash的缓存值
     *
     * @param name  hash name
     * @param key   hash key
     * @param value hash value
     * @return void
     * @author : zhicong.lin
     * @date : 2022/2/4 11:42
     */
    public void hset(String name, Object key, Object value) {
        hset(name, key, value, DEFAULT_EXPIRE, DEFAULT_TIME_UNIT);
    }

    /**
     * 设置name为DEFAULT_CACHE_NAME的缓存值
     *
     * @param key
     * @param value
     * @return void
     * @author : zhicong.lin
     * @date : 2022/2/4 11:43
     */
    public void hset(Object key, Object value) {
        hset(DEFAULT_CACHE_NAME, key, value);
    }

    /**
     * 获取hash的缓存值
     *
     * @param name hash name
     * @param key  hash key
     * @return Object
     * @author : zhicong.lin
     * @date : 2022/2/4 11:42
     */
    public Object hget(String name, Object key) {
        final HashOperations<String, Object, Object> hashOperations = RedisManager.getRedisTemplate().opsForHash();
        return hashOperations.get(name, key);
    }

    /**
     * 获取hash的缓存列表值
     *
     * @param name hash name
     * @return List<Object>
     * @author : zhicong.lin
     * @date : 2022/2/4 11:42
     */
    public List<Object> hgetList(String name) {
        final HashOperations<String, Object, Object> hashOperations = RedisManager.getRedisTemplate().opsForHash();
        return hashOperations.values(name);
    }

    public Object hget(Object key) {
        return hget(DEFAULT_CACHE_NAME, key);
    }

    /**
     * 设置缓存
     *
     * @param key   KEY
     * @param value 值
     * @param time  时间
     * @param unit  时间单位
     * @return void
     * @author : zhicong.lin
     * @date : 2022/2/4 11:36
     */
    public void set(String key, Object value, long time, TimeUnit unit) {
        final ValueOperations<String, Object> opsForValue = RedisManager.getRedisTemplate().opsForValue();
        opsForValue.set(key, value, time, unit);
    }

    /**
     * 设置参数key的值
     *
     * @param key
     * @param value
     * @return void
     * @author : zhicong.lin
     * @date : 2022/2/4 11:36
     */
    public void set(String key, Object value) {
        set(key, value, DEFAULT_EXPIRE, DEFAULT_TIME_UNIT);
    }

    /**
     * 获取参数key的值
     *
     * @param key
     * @return java.lang.Object
     * @author : zhicong.lin
     * @date : 2022/2/4 11:36
     */
    public Object get(String key) {
        return RedisManager.getRedisTemplate().opsForValue().get(key);
    }

    /**
     * 从右边放入
     *
     * @param key
     * @param value
     * @return void
     * @author : zhicong.lin
     * @date : 2022/2/4 11:35
     */
    public void rpush(String key, Object value) {
        RedisManager.getRedisTemplate().opsForList().rightPushAll(key, value);
        RedisManager.getRedisTemplate().expire(key, DEFAULT_EXPIRE, DEFAULT_TIME_UNIT);
    }

    /**
     * 从左边取出来
     *
     * @param key
     * @return java.lang.Object
     * @author : zhicong.lin
     * @date : 2022/2/4 11:35
     */
    public Object lpop(String key) {
        return RedisManager.getRedisTemplate().opsForList().leftPop(key);
    }

    /**
     * 获取列表
     *
     * @param key
     * @return java.util.List<java.lang.Object>
     * @author : zhicong.lin
     * @date : 2022/2/4 11:34
     */
    public List<Object> list(String key) {
        return list(key, 0, -1);
    }

    /**
     * 获取列表
     *
     * @param key
     * @param start
     * @param end
     * @return java.util.List<java.lang.Object>
     * @author : zhicong.lin
     * @date : 2022/2/4 11:34
     */
    public List<Object> list(String key, long start, long end) {
        return RedisManager.getRedisTemplate().opsForList().range(key, start, end);
    }

    /**
     * 删除缓存
     *
     * @param key 删除的key数组
     * @return void
     * @author : zhicong.lin
     * @date : 2022/2/4 11:21
     */
    public void del(String... key) {
        RedisManager.getRedisTemplate().delete(Arrays.asList(key));
    }

    /**
     * 删除缓存
     *
     * @param keys
     * @return void
     * @author : zhicong.lin
     * @date : 2022/2/4 11:19
     */
    public void del(Collection<String> keys) {
        RedisManager.getRedisTemplate().delete(keys);
    }

    /**
     * 获取全部缓存name开头的key
     *
     * @param name 缓存name
     * @return Set<String>
     * @author : zhicong.lin
     * @date : 2022/2/4 11:18
     */
    public Set<String> keys(String name) {
        return RedisManager.getRedisTemplate().keys(name + "*");
    }

    /**
     * 删除缓存HASH
     *
     * @param name 缓存hash的name
     * @param key  缓存key
     * @return void
     * @author : zhicong.lin
     * @date : 2022/2/4 11:17
     */
    public void remove(String name, Object... key) {
        RedisManager.getRedisTemplate().opsForHash().delete(name, key);
    }

    /**
     * 设置过期时间
     *
     * @param key  缓存key
     * @param time 时间
     * @param unit 时间单位
     * @return void
     * @author : zhicong.lin
     * @date : 2022/2/4 11:16
     */
    public void expire(String key, long time, TimeUnit unit) {
        RedisManager.getRedisTemplate().expire(key, time, unit);
    }

}
