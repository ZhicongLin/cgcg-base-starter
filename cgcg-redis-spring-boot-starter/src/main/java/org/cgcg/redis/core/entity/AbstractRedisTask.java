package org.cgcg.redis.core.entity;

import com.cgcg.context.SpringContextHolder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;

/**
 * 定时任务缓存锁.
 *
 * @Author: ZhiCong.Lin
 * @Create: 2018-06-22 13:43
 */
@Slf4j
@Setter
@Getter
public abstract class AbstractRedisTask {
    private String lockKey;
    private String lockValue = "DEFAULT_LOCK_VALUE";
    private int fixedTime;
    private RedisHelper redisHelper;
    private boolean async;

    /**
     * 任务缓存锁.
     *
     * @Author: ZhiCong Lin
     */
    public AbstractRedisTask(RedisHelper redisHelper, boolean async) {
        this(redisHelper, null, async);
    }

    /**
     * 任务缓存锁.
     *
     * @Author: ZhiCong Lin
     */
    public AbstractRedisTask() {
        this(null);
    }

    /**
     * 任务缓存锁.
     *
     * @Author: ZhiCong Lin
     */
    public AbstractRedisTask(RedisHelper redisHelper) {
        this(redisHelper, null, false);
    }

    /**
     * 任务缓存锁.
     *
     * @param lockKey
     * @Author: ZhiCong Lin
     */
    public AbstractRedisTask(RedisHelper redisHelper, String lockKey, boolean async) {
        this(redisHelper, lockKey, 0, async);
    }

    /**
     * 任务缓存锁.
     *
     * @param lockKey
     * @Author: ZhiCong Lin
     */
    public AbstractRedisTask(RedisHelper redisHelper, String lockKey) {
        this(redisHelper, lockKey, 0, false);
    }

    /**
     * 任务缓存锁
     *
     * @param lockKey
     * @param fixedTime
     */
    public AbstractRedisTask(RedisHelper redisHelper, String lockKey, int fixedTime, boolean async) {
        this.redisHelper = redisHelper;
        if (this.redisHelper == null) {
            this.redisHelper = SpringContextHolder.getBean(RedisHelper.class);
        }
        this.async = async;
        if (lockKey == null) {
            final StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            this.lockKey = stackTraceElement.getClassName() + stackTraceElement.getMethodName() + stackTraceElement.getLineNumber();
        } else {
            this.lockKey = lockKey;
        }
        if (fixedTime > 0) {
            this.fixedTime = fixedTime;
        }
        if (async) {
            final ExecutorService executorService = SpringContextHolder.getBean("cacheExecutor");
            executorService.execute(this::build);
        } else {
            build();
        }
    }

    /**
     * 执行任务 .
     *
     * @Return: void
     * @Author: ZhiCong Lin
     * @Date: 2018/6/25 17:12
     */
    private void build() {
        boolean lock = false;
        try {
            lock = redisHelper.lock(lockKey, fixedTime);
            if (lock) {
                execute();
            } else {
                log.info("The task is cancelled because the refresh task is being executed.");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (lock) {
                redisHelper.delete(lockKey);
            }
        }
    }

    /**
     * 执行具体功能的方法 .
     *
     * @Return: void
     * @Author: ZhiCong Lin
     * @Date: 2018/6/22 13:51
     */
    protected abstract void execute();


    /**
     * 执行任务（同步）
     *
     * @param callback 回调的类
     * @author : zhicong.lin
     * @date : 2022/2/2 10:58
     */
    public static void execute(final Callback callback) {
        execute(null, callback);
    }

    /**
     * 执行任务（同步）
     *
     * @param lockKey  缓存锁的key （默认类名+方法名+行数）
     * @param callback 回调的类
     * @author : zhicong.lin
     * @date : 2022/2/2 10:58
     */
    public static void execute(String lockKey, final Callback callback) {
        execute(lockKey, 0, callback, false);
    }

    /**
     * 执行任务（异步）
     *
     * @param lockKey  缓存锁的key （默认类名+方法名+行数）
     * @param callback 回调的类
     * @return void
     * @author : zhicong.lin
     * @date : 2022/2/2 10:57
     */
    public static void executeAsync(String lockKey, final Callback callback) {
        execute(lockKey, 0, callback, true);
    }

    /**
     * 执行任务
     *
     * @param lockKey   缓存锁的key （默认类名+方法名+行数）
     * @param fixedTime 缓存锁时间 （默认100毫秒）
     * @param callback  回调的类
     * @param async     是否异步执行
     * @return void
     * @author : zhicong.lin
     * @date : 2022/2/2 10:50
     */
    public static void execute(String lockKey, int fixedTime, final Callback callback, boolean async) {
        final RedisHelper redisHelper = SpringContextHolder.getBean(RedisHelper.class);
        new AbstractRedisTask(redisHelper, lockKey, fixedTime, async) {
            @Override
            public void execute() {
                if (callback != null) {
                    callback.execute();
                }
            }
        };
    }

}
