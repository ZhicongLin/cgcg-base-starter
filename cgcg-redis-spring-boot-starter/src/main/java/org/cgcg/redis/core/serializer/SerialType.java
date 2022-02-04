package org.cgcg.redis.core.serializer;

/**
 * @author zhicong.lin
 */

public enum SerialType {
    /**
     * FastJsonRedisSerializer
     */
    JSON,
    /**
     * JdkSerializationRedisSerializer
     */
    JDK,
    /**
     * FastSerializationRedisSerializer
     */
    FST,
    /**
     * KryoRedisSerializer
     */
    KRYO
}
