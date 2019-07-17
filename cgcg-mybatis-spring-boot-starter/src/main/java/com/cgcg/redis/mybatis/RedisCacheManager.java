package com.cgcg.redis.mybatis;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.Cache;
import org.cgcg.redis.core.RedisManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * redis缓存管理工具.
 *
 * @author zhicong.lin
 * @date 2019/6/24
 */
@Slf4j
public class RedisCacheManager implements Cache {
    // 缓存默认过期时间为30分钟
    private final static int DEFAULT_EXPIRE = 1800;
    // 读写锁
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    private RedisTemplate<String, Object> redisTemplate;
    private String id;

    public RedisCacheManager(final String id) {
        Assert.notNull(id, "require id must not null");
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }
    @Override
    public void putObject(Object cacheKey, Object cacheValue) {
        final String key = id + ":" + cacheKey.toString();
        log.debug("Redis Put {}" , key);
        getRedisTemplate().opsForValue().set(key, cacheValue);
        getRedisTemplate().expire(key, DEFAULT_EXPIRE, TimeUnit.SECONDS);
    }

    @Override
    public Object getObject(Object cacheKey) {
        final String key = id + ":" + cacheKey.toString();
        log.debug("Redis Get {}" , key);
        return getRedisTemplate().opsForValue().get(key);
    }

    @Override
    public Object removeObject(Object cacheKey) {
        final String key = id + ":" + cacheKey.toString();
        log.debug("Redis Remvoe {}" , key);
        return getRedisTemplate().delete(key);
    }

    @Override
    public void clear() {
        log.debug("Redis Clear");
        final Set<String> keys = getRedisTemplate().keys(id + ":*");
        if (keys != null && !keys.isEmpty()) {
            getRedisTemplate().delete(keys);
        }
    }

    @Override
    public int getSize() {
        final Set<String> keys = getRedisTemplate().keys(id + ":*");
        return keys == null ? 0 : keys.size();
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return readWriteLock;
    }

    public RedisTemplate<String, Object> getRedisTemplate() {
        if (redisTemplate == null) {
            redisTemplate = RedisManager.getRedisTemplate();
        }
        return redisTemplate;
    }
}
