package org.cgcg.redis.core;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import com.cgcg.context.SpringContextHolder;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

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
     *
     * @return the redis template
     */
    public static RedisTemplate<String, Object> getCurrent() {
        if (RedisTemplateHolder.redisTemplate == null) {
            synchronized (RedisTemplateHolder.class) {
                if (RedisTemplateHolder.redisTemplate == null) {
                    final RedisConnectionFactory connectionFactory = SpringContextHolder.getBean(RedisConnectionFactory.class);
                    RedisTemplateHolder.redisTemplate = RedisManager.createTemplate(connectionFactory);
                }
            }
        }
        return RedisTemplateHolder.redisTemplate;
    }

    /**
     * 创建RedisTemplate<String, Object>
     *
     * @param redisConnectionFactory
     * @return RedisTemplate<String, Object>
     */
    public static RedisTemplate<String, Object> createTemplate(RedisConnectionFactory redisConnectionFactory) {

        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 配置连接工厂
        template.setConnectionFactory(redisConnectionFactory);

        //使用StringRedisSerializer来序列化和反序列化redis的key值
        final StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        // 设置hash key 和value序列化模式
        template.setHashKeySerializer(stringRedisSerializer);

        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
        final Jackson2JsonRedisSerializer<Object> jacksonSerial = new Jackson2JsonRedisSerializer<>(Object.class);
        final ObjectMapper om = new ObjectMapper();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jacksonSerial.setObjectMapper(om);

        // 值采用json序列化
        template.setValueSerializer(jacksonSerial);
        template.setHashValueSerializer(jacksonSerial);
        template.afterPropertiesSet();
        return template;
    }

    static class RedisTemplateHolder {
        private static volatile RedisTemplate<String, Object> redisTemplate;
    }

}
