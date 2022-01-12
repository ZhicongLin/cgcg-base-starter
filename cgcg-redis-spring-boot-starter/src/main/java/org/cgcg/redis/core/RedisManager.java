package org.cgcg.redis.core;

import com.cgcg.context.SpringContextHolder;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
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
@EnableScheduling
public class RedisManager {
    public static boolean online = false;
    @Resource
    private RedisProperties redisProperties;

    /**
     * Gets redis template.
     *
     * @return the redis template
     */
    public static RedisTemplate<String, Object> getRedisTemplate() {
        if (RedisTemplateHolder.redisTemplate == null) {
            synchronized (RedisTemplateHolder.class) {
                if (RedisTemplateHolder.redisTemplate == null) {
                    RedisTemplateHolder.redisTemplate = new RedisTemplate<>();
                    RedisTemplateHolder.redisTemplate.setConnectionFactory(SpringContextHolder.getBean(RedisConnectionFactory.class));
                    // 使用Jackson2JsonRedisSerialize 替换默认序列化
                    final Jackson2JsonRedisSerializer<?> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
                    objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

                    jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
//                    JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();
                    RedisTemplateHolder.redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
                    RedisTemplateHolder.redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
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

    @Scheduled(fixedDelay = 5000L)
    public void hearts() {

        try {
            Jedis jedis = new Jedis(this.redisProperties.getHost(), this.redisProperties.getPort());
            if (this.redisProperties.getPassword() != null) {
                jedis.auth(this.redisProperties.getPassword());
            }
            online = jedis.ping().equalsIgnoreCase("PONG");
        } catch (Exception e) {
            online = false;
            log.warn("Redis[{}:{}]缓存服务不在线！", this.redisProperties.getHost(), this.redisProperties.getPort());
        }
    }
}
