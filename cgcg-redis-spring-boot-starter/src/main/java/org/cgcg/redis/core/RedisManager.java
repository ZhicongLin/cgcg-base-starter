package org.cgcg.redis.core;

import com.cgcg.context.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.cgcg.redis.core.serializer.KryoRedisSerializer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.concurrent.*;

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
            RedisTemplateHolder.redisTemplate = new RedisTemplate<>();
            RedisTemplateHolder.redisTemplate.setConnectionFactory(SpringContextHolder.getBean(RedisConnectionFactory.class));
            // 设置键（key）的序列化采用StringRedisSerializer。
            final RedisSerializer<String> keySerializer = RedisSerializer.string();
            RedisTemplateHolder.redisTemplate.setKeySerializer(keySerializer);
            RedisTemplateHolder.redisTemplate.setHashKeySerializer(keySerializer);
            // KryoRedisSerializer 替换默认序列化
            final KryoRedisSerializer<?> valueSerializer = new KryoRedisSerializer<>();
            RedisTemplateHolder.redisTemplate.setValueSerializer(valueSerializer);
            RedisTemplateHolder.redisTemplate.setHashValueSerializer(valueSerializer);
            RedisTemplateHolder.redisTemplate.afterPropertiesSet();
        }
        return RedisTemplateHolder.redisTemplate;
    }

    static class RedisTemplateHolder {
        private static volatile RedisTemplate<String, Object> redisTemplate;
    }

    @Bean("cacheExecutor")
    public ExecutorService executorService() {
        // new SynchronousQueue<Runnable>() 队列没有空间，表示一旦有任务就马上会被执行, 这里会无限制的开辟线程，适合时间较短的操作
        return Executors.newCachedThreadPool();
    }

    @Bean("executorMQService")
    public ExecutorService executorMqService() {
        final int corePoolSize = Runtime.getRuntime().availableProcessors();
        final int maximumPoolSize = corePoolSize * 2 + 1;
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 180L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), r -> new Thread(r));
    }

    @Scheduled(fixedDelay = 5000L)
    public void hearts() {
        try {
            Jedis jedis = new Jedis(this.redisProperties.getHost(), this.redisProperties.getPort());
            if (this.redisProperties.getPassword() != null) {
                jedis.auth(this.redisProperties.getPassword());
            }
            online = "PONG".equalsIgnoreCase(jedis.ping());
        } catch (Exception e) {
            online = false;
            log.warn("Redis[{}:{}]缓存服务不在线！", this.redisProperties.getHost(), this.redisProperties.getPort());
        }
    }
}
