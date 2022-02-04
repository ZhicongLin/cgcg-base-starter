package org.cgcg.redis.core.penetrate;

import org.cgcg.redis.core.RedisManager;
import org.cgcg.redis.core.entity.AbstractRedisTask;
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
     * 每半个小时清理掉缓存不再穿透的KEY。
     *
     * @return void
     * @author : zhicong.lin
     * @date : 2022/2/2 9:46
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    public void scheduled() {
        //使用RedisTask自带分布式缓存锁，防止多次执行
        AbstractRedisTask.execute(() -> {
            final Set<Object> keys = RedisManager.getRedisTemplate().opsForHash().keys(RedisPenetrate.PENETRATE_KEY);
            if (!keys.isEmpty()) {
                RedisPenetrate.clear(keys);
            }
        });
    }
}
