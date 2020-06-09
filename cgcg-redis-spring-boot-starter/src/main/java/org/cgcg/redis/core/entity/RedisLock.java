package org.cgcg.redis.core.entity;

import java.util.concurrent.TimeUnit;

import org.cgcg.redis.core.RedisManager;
import org.cgcg.redis.core.exception.RedisLockException;
import org.springframework.data.redis.core.RedisTemplate;

import lombok.Getter;
import lombok.Setter;

/**
 * redis分布式锁的实现
 */
@Setter
@Getter
public class RedisLock {

    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 锁的后缀
     */
    private static final String LOCK_SUFFIX = "::lock";

    private static final String DEFAULT_VAL = "1";

    /**
     * 锁的key
     */
    private String lockKey;

    /**
     * 锁超时时间，防止线程在入锁以后，防止阻塞后面的线程无法获取锁
     */
    private long expireMsecs = 100;

    /**
     * 是否锁定标志
     */
    private volatile boolean locked = false;

    /**
     * 构造器
     * @param lockKey 锁的key
     */
    public RedisLock(String lockKey) {
        this.redisTemplate = RedisManager.getRedisTemplate();
        this.lockKey = lockKey + LOCK_SUFFIX;
    }

    /**
     * 构造器
     * @param lockKey 锁的key
     * @param expireMsecs 获取锁的超时时间
     */
    public RedisLock(String lockKey, long expireMsecs) {
        this(lockKey);
        this.expireMsecs = expireMsecs;
    }

    /**
     * 获取普通锁，锁会根据时间自动过期解锁，默认100毫秒
     *
     * @return 获取锁成功返回ture，超时返回false
     */
    public synchronized boolean lock() {
        Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, DEFAULT_VAL);
        redisTemplate.expire(lockKey, expireMsecs, TimeUnit.MILLISECONDS);
        locked = result != null && result;
        return locked;
    }

    /**
     * 获取一个永久锁，需要手动解锁
     *
     * @return 获取锁成功返回ture，超时返回false
     */
    public synchronized boolean foreverLock() {
        Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, DEFAULT_VAL);
        locked = result != null && result;
        return locked;
    }

    /**
     * 释放获取到的锁
     */
    public synchronized void unlock() {
        if (locked) {
            redisTemplate.delete(lockKey);
            locked = false;
        }
    }

    public static void lockHear(String lockKey) throws RedisLockException {
        final RedisLock redisLock = new RedisLock(lockKey);
        if (!redisLock.lock()) {
            throw new RedisLockException();
        }
    }

    public static void lockHear(String lockKey, long timeMillis) throws RedisLockException {
        final RedisLock redisLock = new RedisLock(lockKey, timeMillis);
        if (!redisLock.lock()) {
            throw new RedisLockException();
        }
    }

    public static void lockHearForever(String lockKey) throws RedisLockException {
        final RedisLock redisLock = new RedisLock(lockKey);
        if (!redisLock.foreverLock()) {
            throw new RedisLockException();
        }
    }
}
