package com.cgcg.jobs.thread;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class QueueRunner {
    @Resource
    private QueueStopTaskRunner queueStopTaskRunner;
    @Resource
    private QueueRestartRunner queueRestartRunner;

    @Bean
    public ExecutorService executorService() {
        final ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(2, 5, 2L, TimeUnit.MINUTES, new LinkedBlockingQueue<>());
        poolExecutor.execute(queueRestartRunner);
        poolExecutor.execute(queueStopTaskRunner);
        return poolExecutor;
    }

}
