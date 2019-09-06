package com.cgcg.context.thread;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolConfiguration {

    @Bean
    public LinkedBlockingQueue<Runnable> linkedBlockingQueue() {
        return new LinkedBlockingQueue<>();
    }

    @Bean
    public ThreadPoolExecutor.AbortPolicy abortPolicy() {
        return new ThreadPoolExecutor.AbortPolicy();
    }

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(LinkedBlockingQueue<Runnable> linkedBlockingQueue, ThreadPoolExecutor.AbortPolicy abortPolicy) {
        return ThreadPoolManager.createExecutor(linkedBlockingQueue, abortPolicy);
    }
}
