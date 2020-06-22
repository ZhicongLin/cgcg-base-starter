package org.cgcg.redis.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * Description: 配置
 *
 * @author linzc
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本           修改人       修改日期         修改内容
 * 2020/6/5.1       linzc    2020/6/5           Create
 * </pre>
 * @date 2020/6/5
 */
@Slf4j
@Configuration
public class RedisAutoConfiguration {

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        return RedisManager.getCurrent();
    }

    @Bean("cacheExecutor")
    public ExecutorService cacheExecutorService() {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 120L, TimeUnit.SECONDS, new SynchronousQueue<>());
    }
}