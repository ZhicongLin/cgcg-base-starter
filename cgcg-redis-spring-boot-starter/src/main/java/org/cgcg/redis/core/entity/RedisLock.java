package org.cgcg.redis.core.entity;

import org.springframework.data.redis.core.RedisTemplate;

/**
 * redis分布式锁的实现
 */
public class RedisLock {

    private RedisTemplate redisTemplate;

    /**
     * 锁的后缀
     */
    private static final String LOCK_SUFFIX = "redis_lock";

    /**
     * 锁的key
     */
    private String lockKey;

    /**
     * 锁超时时间，防止线程在入锁以后，防止阻塞后面的线程无法获取锁
     */
    private int expireMsecs = 300;

    /**
     * 线程获取锁的等待时间
     */
    private int timeoutMsecs = 10 * 1000;

    /**
     * 是否锁定标志
     */
    private volatile boolean locked = false;

    /**
     * 构造器
     * @param redisTemplate
     * @param lockKey 锁的key
     */
    public RedisLock(RedisTemplate redisTemplate, String lockKey) {
        this.redisTemplate = redisTemplate;
        this.lockKey = lockKey + LOCK_SUFFIX;
    }

    /**
     * 构造器
     * @param redisTemplate
     * @param lockKey 锁的key
     * @param timeoutMsecs 获取锁的超时时间
     */
    public RedisLock(RedisTemplate redisTemplate, String lockKey, int timeoutMsecs) {
        this(redisTemplate, lockKey);
        this.timeoutMsecs = timeoutMsecs;
    }

    /**
     * 构造器
     * @param redisTemplate
     * @param lockKey 锁的key
     * @param timeoutMsecs 获取锁的超时时间
     * @param expireMsecs 锁的有效期
     */
    public RedisLock(RedisTemplate redisTemplate, String lockKey, int timeoutMsecs, int expireMsecs) {
        this(redisTemplate, lockKey, timeoutMsecs);
        this.expireMsecs = expireMsecs;
    }

    public String getLockKey() {
        return lockKey;
    }

    /**
     * 封装和jedis方法
     * @param key
     * @return
     */
    private String get(final String key) {
        Object obj = redisTemplate.opsForValue().get(key);
        return obj != null ? obj.toString() : null;
    }

    /**
     * 封装和jedis方法
     * @param key
     * @param value
     * @return
     */
    private boolean setNX(final String key, final String value) {
        return redisTemplate.opsForValue().setIfAbsent(key,value);
    }

    /**
     * 封装和jedis方法
     * @param key
     * @param value
     * @return
     */
    private String getSet(final String key, final String value) {
        Object obj = redisTemplate.opsForValue().getAndSet(key,value);
        return obj != null ? (String) obj : null;
    }

    /**
     * 获取锁
     * @return 获取锁成功返回ture，超时返回false
     * @throws InterruptedException
     */
    public synchronized boolean lock() throws InterruptedException {
        long expires = System.currentTimeMillis() + expireMsecs + 1;
        //锁到期时间
        String expiresStr = String.valueOf(expires);
        if (this.setNX(lockKey, expiresStr)) {
            locked = true;
            return true;
        }
        //redis里key的时间
        String currentValue = this.get(lockKey);
        //判断锁是否已经过期，过期则重新设置并获取
        if (currentValue != null && Long.parseLong(currentValue) < System.currentTimeMillis()) {
            //设置锁并返回旧值
            String oldValue = this.getSet(lockKey, expiresStr);
            //比较锁的时间，如果不一致则可能是其他锁已经修改了值并获取
            if (oldValue != null && oldValue.equals(currentValue)) {
                locked = true;
                return true;
            }
        }
        return false;
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
}
