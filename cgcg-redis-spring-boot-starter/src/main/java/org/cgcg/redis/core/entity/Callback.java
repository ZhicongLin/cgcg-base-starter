package org.cgcg.redis.core.entity;

/**
 * Redis任务回调接口.
 *
 * @author zhicong.lin
 * @date 2019/6/26
 */
public interface Callback {

    /**
     * redis回调执行入口
     *
     * @return void
     * @author zhicong.lin 2022/1/26
     */
    void execute();
}
