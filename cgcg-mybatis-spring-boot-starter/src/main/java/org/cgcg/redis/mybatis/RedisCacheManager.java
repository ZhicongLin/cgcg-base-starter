package org.cgcg.redis.mybatis;

import org.apache.ibatis.cache.Cache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * redis缓存管理工具.
 *
 * @author zhicong.lin
 * @date 2019/6/24
 */
public class RedisCacheManager implements Cache {
    // 读写锁
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

    private RedisTemplate<String, Object> redisCacheTemplate;

    private String id;

    public RedisCacheManager(final String id) {
        Assert.notNull(id, "require id must not null");
        this.id =  id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void putObject(Object cacheKey, Object cacheValue) {
        getRedisCacheTemplate().opsForHash().put(id, cacheKey.toString(), cacheValue);
        getRedisCacheTemplate().expire(id, 2L, TimeUnit.HOURS);
    }

    @Override
    public Object getObject(Object cacheKey) {
        return getRedisCacheTemplate().opsForHash().get(id, cacheKey.toString());
    }

    @Override
    public Object removeObject(Object cacheKey) {
        return getRedisCacheTemplate().opsForHash().delete(id, cacheKey.toString());
    }

    @Override
    public void clear() {
        getRedisCacheTemplate().delete(id);
    }

    @Override
    public int getSize() {
        final Long size = getRedisCacheTemplate().opsForHash().size(id);
        return size.intValue();
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return readWriteLock;
    }

    private RedisTemplate<String, Object> getRedisCacheTemplate() {
        if (this.redisCacheTemplate == null) {
            this.redisCacheTemplate = CgBeanHolder.getBean("redisCacheTemplate");
        }
        return redisCacheTemplate;
    }
}
