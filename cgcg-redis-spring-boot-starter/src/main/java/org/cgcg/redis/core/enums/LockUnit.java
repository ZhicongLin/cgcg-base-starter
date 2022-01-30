package org.cgcg.redis.core.enums;

/**
 * 是否使用缓存锁枚举.
 *
 * @author zhicong.lin
 * @date 2019/6/21
 */
public enum LockUnit {

    /**
     * 使用缓存锁
     */
    LOCK,

    /**
     * 不使用缓存锁
     */
    UNLOCK,

    /**
     * 不使用缓存锁
     */
    NULL;
}
