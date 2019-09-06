package com.cgcg.context.thread;

import com.cgcg.context.SpringContextHolder;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.*;

/**
 * 线程池工具类
 */
@Setter
@Getter
public class ThreadPoolManager {

    /**
     * 根据cpu的数量动态的配置核心线程数和最大线程数
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    /**
     * 非核心线程闲置时超时10s
     */
    private static final int KEEP_ALIVE = 10;
    private static ThreadPoolManager poolManager;
    /**
     * 线程池的对象
     */
    private ThreadPoolExecutor executor;

    /**
     * 要确保该类只有一个实例对象，避免产生过多对象消费资源，所以采用单例模式
     */
    private ThreadPoolManager() {
    }

    private synchronized static ThreadPoolManager getInstance() {
        if (poolManager == null) {
            poolManager = new ThreadPoolManager();
        }
        return poolManager;
    }

    public static ThreadPoolExecutor createExecutor(LinkedBlockingQueue<Runnable> linkedBlockingQueue, ThreadPoolExecutor.AbortPolicy abortPolicy) {
        // * corePoolSize:核心线程数
        // * maximumPoolSize：线程池所容纳最大线程数(workQueue队列满了之后才开启)
        // * keepAliveTime：非核心线程闲置时间超时时长
        // * unit：keepAliveTime的单位
        // * workQueue：等待队列，存储还未执行的任务
        // * threadFactory：线程创建的工厂
        // * handler：异常处理机制
        return new ThreadPoolExecutor( CPU_COUNT + 1, CPU_COUNT * 2 + 1, KEEP_ALIVE, TimeUnit.SECONDS,
                linkedBlockingQueue, Executors.defaultThreadFactory(), abortPolicy);
    }

    /**
     * 获得线程池的大小
     *
     * @return
     */
    public int getPoolSize() {
        return getThreadPoolExecutor().getPoolSize();
    }

    public static void execute(Runnable r) {
        getThreadPoolExecutor().execute(r);
    }

    private static ThreadPoolExecutor getThreadPoolExecutor() {
        final ThreadPoolManager poolManager = getInstance();
        ThreadPoolExecutor executor = poolManager.getExecutor();
        if (executor == null) {
            final ThreadPoolExecutor bean = SpringContextHolder.getBean(ThreadPoolExecutor.class);
            executor = bean != null ? bean : createExecutor(new LinkedBlockingQueue<>(), new ThreadPoolExecutor.AbortPolicy());
            poolManager.setExecutor(executor);
        }
        return executor;
    }

    /**
     * 开启一个有返回结果的线程
     *
     * @param r
     * @return
     */
    public static <T> Future<T> submit(Callable<T> r) {
        // 把一个任务丢到了线程池中
        return getThreadPoolExecutor().submit(r);
    }

    /**
     * 把任务移除等待队列
     *
     * @param task
     */
    public static void cancel(Runnable task) {
        if (task != null) {
            if (task instanceof ExecutorTask) {
                final ExecutorTask executorTask = (ExecutorTask) task;
                if (!executorTask.isFinish()) {
                    executorTask.cancelTask();
                }
                getThreadPoolExecutor().getQueue().remove(executorTask);
            } else {
                getThreadPoolExecutor().getQueue().remove(task);
            }
        }
    }

}