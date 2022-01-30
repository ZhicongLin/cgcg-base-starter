package org.cgcg.redis.core.exception;
/**
 * @author zhicong.lin
 */
public class RedisExpireException extends RuntimeException {

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public RedisExpireException(String expire) {
        super("Redis缓存Expire时间配置取值异常[" + expire + "]");
    }
}
