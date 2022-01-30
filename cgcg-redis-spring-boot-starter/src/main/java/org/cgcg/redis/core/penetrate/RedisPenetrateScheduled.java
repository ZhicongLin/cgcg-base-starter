package org.cgcg.redis.core.penetrate;

import org.cgcg.redis.core.RedisManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 缓存穿透定时处理器
 *
 * @author zhicong.lin
 * @date 2022/1/17
 */
@Component
@EnableScheduling
public class RedisPenetrateScheduled {
    /**
     * 每个小时清理掉缓存不再穿透的KEY。
     */
    @Scheduled(fixedDelay = 3600000L)
    public void scheduled() {
        final Set<Object> keys = RedisManager.getRedisTemplate().opsForHash().keys(RedisPenetrate.PENETRATE_KEY);
        if (!keys.isEmpty()) {
            RedisPenetrate.clear(keys);
        }
    }
}
