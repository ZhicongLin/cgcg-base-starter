package org.cgcg.redis.core.entity;

/**
 * Redis任务回调接口.
 *
 * @author zhicong.lin
 * @date 2019/6/26
 */
public interface Callback {

   default void execute() {};
}
