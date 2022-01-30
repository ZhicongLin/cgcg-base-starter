package com.cgcg.context.thread;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池相关配置
 * @author zhicong.lin
 */
@Configuration
public class ThreadPoolConfiguration {

    @Bean
    @Primary
    public LinkedBlockingQueue<Runnable> linkedBlockingQueue() {
        return new LinkedBlockingQueue<>();
    }

    @Bean
    @Primary
    public ThreadPoolExecutor.AbortPolicy abortPolicy() {
        return new ThreadPoolExecutor.AbortPolicy();
    }

    @Bean
    @Primary
    public ThreadPoolExecutor threadPoolExecutor(LinkedBlockingQueue<Runnable> linkedBlockingQueue, ThreadPoolExecutor.AbortPolicy abortPolicy) {
        return ThreadPoolManager.createExecutor(linkedBlockingQueue, abortPolicy);
    }
}
