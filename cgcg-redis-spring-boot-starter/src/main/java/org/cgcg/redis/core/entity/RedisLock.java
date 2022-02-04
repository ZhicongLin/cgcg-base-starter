package org.cgcg.redis.core.entity;

import lombok.Getter;
import lombok.Setter;
import org.cgcg.redis.core.RedisManager;
import org.cgcg.redis.core.exception.RedisLockException;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * redis分布式锁的实现
 *
 * @author zhicong.lin
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
     *
     * @param lockKey 锁的key
     */
    public RedisLock(String lockKey) {
        this.redisTemplate = RedisManager.getRedisTemplate();
        this.lockKey = lockKey + LOCK_SUFFIX;
    }

    /**
     * 构造器
     *
     * @param lockKey     锁的key
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
    public boolean lock() {
        if (expireMsecs == 0) {
            expireMsecs = 100;
        }
        Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, DEFAULT_VAL, expireMsecs, TimeUnit.MILLISECONDS);
        locked = result != null && result;
        return locked;
    }

    /**
     * 获取一个永久锁，需要手动解锁
     *
     * @return 获取锁成功返回ture，超时返回false
     */
    public boolean foreverLock() {
        Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, DEFAULT_VAL);
        locked = result != null && result;
        return locked;
    }

    /**
     * 释放获取到的锁
     */
    public void unlock() {
        if (locked) {
            redisTemplate.delete(lockKey);
            locked = false;
        }
    }

    /**
     * 普通锁，默认100ms自动解锁
     *
     * @param lockKey
     * @return void
     * @author : zhicong.lin
     * @date : 2022/2/4 11:40
     */
    public static void lockHear(String lockKey) throws RedisLockException {
        final RedisLock redisLock = new RedisLock(lockKey);
        if (!redisLock.lock()) {
            throw new RedisLockException();
        }
    }

    /**
     * 普通锁，需要设置自动解锁时间
     *
     * @param lockKey
     * @param timeMillis
     * @return void
     * @author : zhicong.lin
     * @date : 2022/2/4 11:40
     */
    public static void lockHear(String lockKey, long timeMillis) throws RedisLockException {
        final RedisLock redisLock = new RedisLock(lockKey, timeMillis);
        if (!redisLock.lock()) {
            throw new RedisLockException();
        }
    }

    /**
     * 永久锁
     *
     * @param lockKey
     * @return void
     * @author : zhicong.lin
     * @date : 2022/2/4 11:40
     */
    public static void lockHearForever(String lockKey) throws RedisLockException {
        final RedisLock redisLock = new RedisLock(lockKey);
        if (!redisLock.foreverLock()) {
            throw new RedisLockException();
        }
    }
}
