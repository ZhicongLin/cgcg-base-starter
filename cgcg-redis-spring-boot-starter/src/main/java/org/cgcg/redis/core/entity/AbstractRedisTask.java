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
    protected abstract void execute(); //NOSONAR

    public static void execute(RedisHelper redisHelper, String lockKey, final Callback callback) {
        execute(redisHelper, lockKey, 0, callback, false);
    }

    public static void executeAsync(RedisHelper redisHelper, String lockKey, final Callback callback) {
        execute(redisHelper, lockKey, 0, callback, true);
    }

    public static void execute(RedisHelper redisHelper, String lockKey, int fixedTime, final Callback callback, boolean async) {
        new AbstractRedisTask(redisHelper, lockKey, fixedTime, async) {
            @Override
            public void execute() { //NOSONAR
                callback.execute();
            }
        };
    }

}
