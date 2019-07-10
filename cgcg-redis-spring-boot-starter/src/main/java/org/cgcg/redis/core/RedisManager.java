package org.cgcg.redis.core;

import lombok.extern.slf4j.Slf4j;
import org.cgcg.redis.core.context.SpringCacheHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * .
 *
 * @author zhicong.lin
 * @date 2019/6/20
 */
@Slf4j
@Component
public class RedisManager {
    /**
     * Gets redis template.
     * @return the redis template
     */
    public static RedisTemplate<String, Object> getRedisTemplate() {
        if (RedisTemplateHolder.redisTemplate == null) {
            synchronized (RedisTemplateHolder.class) {
                if (RedisTemplateHolder.redisTemplate == null) {
                    RedisTemplateHolder.redisTemplate = new RedisTemplate<>();
                    RedisTemplateHolder.redisTemplate.setConnectionFactory(SpringCacheHolder.getBean(RedisConnectionFactory.class));
                    JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();
                    RedisTemplateHolder.redisTemplate.setValueSerializer(jdkSerializationRedisSerializer);
                    RedisTemplateHolder.redisTemplate.setHashValueSerializer(jdkSerializationRedisSerializer);
                    // 设置键（key）的序列化采用StringRedisSerializer。
                    StringRedisSerializer serializer = new StringRedisSerializer();
                    RedisTemplateHolder.redisTemplate.setKeySerializer(serializer);
                    RedisTemplateHolder.redisTemplate.setHashKeySerializer(serializer);
                    RedisTemplateHolder.redisTemplate.afterPropertiesSet();
                }
            }
        }
        return RedisTemplateHolder.redisTemplate;
    }

    static class RedisTemplateHolder {
        private static volatile RedisTemplate<String, Object> redisTemplate;
    }

    @Bean("cacheExecutor")
    public ExecutorService executorService() {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                120L, TimeUnit.SECONDS,
                new SynchronousQueue<>());
    }
}
