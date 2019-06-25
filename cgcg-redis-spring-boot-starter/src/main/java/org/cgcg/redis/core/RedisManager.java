package org.cgcg.redis.core;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

/**
 * .
 *
 * @author zhicong.lin
 * @date 2019/6/20
 */
@Slf4j
@Setter
@Getter
@Component
public class RedisManager {

    @Bean("redisCacheTemplate")
    public RedisTemplate<String, Object> redisTemplate(@Autowired RedisConnectionFactory redisConnectionFactory) {
        final RedisTemplate<String, Object>  redisTemplate = new RedisTemplate<>();
        final RedisSerializer<String> serializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(serializer);
        redisTemplate.setHashKeySerializer(serializer);
        // 开启事务
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }


}
