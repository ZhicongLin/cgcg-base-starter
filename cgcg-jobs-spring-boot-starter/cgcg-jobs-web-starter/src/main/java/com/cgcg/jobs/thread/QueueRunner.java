package com.cgcg.jobs.thread;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
 * @author zhicong.lin
 */
@Slf4j
@Component
public class QueueRunner {
    @Resource
    private QueueStopTaskRunner queueStopTaskRunner;
    @Resource
    private QueueRestartRunner queueRestartRunner;

    @Bean
    public ExecutorService executorService() {
        final int corePoolSize = Runtime.getRuntime().availableProcessors();
        final ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(corePoolSize, corePoolSize * 2 + 1, 2, TimeUnit.MINUTES, new LinkedBlockingQueue<>(), r -> new Thread(r));
        poolExecutor.execute(queueRestartRunner);
        poolExecutor.execute(queueStopTaskRunner);
        return poolExecutor;
    }

}
