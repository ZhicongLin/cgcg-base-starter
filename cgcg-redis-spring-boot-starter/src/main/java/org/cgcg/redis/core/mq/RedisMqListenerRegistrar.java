package org.cgcg.redis.core.mq;

import com.cgcg.context.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.cgcg.redis.core.RedisManager;
import org.cgcg.redis.core.annotation.RmqListener;
import org.cgcg.redis.core.annotation.Rmqc;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * @author zhicong.lin
 */
@Slf4j
public class RedisMqListenerRegistrar implements ApplicationListener<ContextRefreshedEvent> {
    @Resource
    @Qualifier("container")
    private RedisMessageListenerContainer listenerContainer;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        final Map<String, Object> beansWithAnnotation = SpringContextHolder.getBeansWithAnnotation(RmqListener.class);
        final Set<Map.Entry<String, Object>> entitySet = beansWithAnnotation.entrySet();
        entitySet.forEach(entry -> this.registerRmqcMethods(entry.getKey()));
    }

    private void registerRmqcMethods(String className) {
        try {
            final Class<?> clazz = Class.forName(className);
            final Method[] methods = clazz.getMethods();
            Arrays.stream(methods).filter(m -> m.isAnnotationPresent(Rmqc.class))
                    .forEach(rm -> this.addListener(clazz, rm, rm.getAnnotation(Rmqc.class).value()));
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void addListener(Class<?> clazz, Method method, String channel) {
        final MessageListenerAdapter adapter;
        try {
            adapter = new MessageListenerAdapter(clazz.newInstance(), method.getName());
            adapter.setSerializer(RedisManager.getRedisTemplate().getValueSerializer());
            adapter.afterPropertiesSet();
            listenerContainer.addMessageListener(adapter, new PatternTopic(channel));
        } catch (InstantiationException | IllegalAccessException e) {
            log.error(e.getMessage(), e);
        }
    }

}