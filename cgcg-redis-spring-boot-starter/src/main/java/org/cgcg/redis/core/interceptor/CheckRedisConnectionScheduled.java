package org.cgcg.redis.core.interceptor;

import javax.annotation.Resource;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Description: 校验缓存链接定时器
 *
 * @author linzc
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期        修改内容
 * 2020/6/22.1    linzc       2020/6/22     Create
 * </pre>
 * @date 2020/6/22
 */
@Slf4j
@Component
@EnableScheduling
public class CheckRedisConnectionScheduled {
    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    /**
     * 每30秒校验一次
     */
    @Scheduled(fixedRate = 30000)
    public void check() {
        try {
            AbstractRedisCacheExecutor.connection = !redisConnectionFactory.getConnection().isClosed();
        } catch (Exception e) {
            log.warn("Miss Redis Cache Server Connection", e);
            AbstractRedisCacheExecutor.connection = false;
        }
    }
}