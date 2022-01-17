package org.cgcg.redis.core.config;

import org.cgcg.redis.core.RedisAspect;
import org.cgcg.redis.core.RedisManager;
import org.cgcg.redis.core.entity.RedisHelper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

@Component
@EnableScheduling
public class RedisDangerousKeyScheduled {
    @Resource
    private RedisHelper redisHelper;
    /**
     * 每个小时清理掉缓存不再穿透的KEY。
     */
    @Scheduled(fixedDelay = 3600000L)
    public void scheduled() {
        final Set<Object> keys = RedisManager.getRedisTemplate().opsForHash().keys(RedisAspect.PENETRATE_KEY);
        if (keys.isEmpty()) {
            return;
        }
        for (Object key : keys) {
            final Object o = redisHelper.get(key.toString());
            if (o != null && !o.equals(RedisAspect.PENETRATE_VALUE)) {
                // 风险解除，清理掉key
                redisHelper.remove(RedisAspect.PENETRATE_KEY, key);
            }
        }
    }
}
