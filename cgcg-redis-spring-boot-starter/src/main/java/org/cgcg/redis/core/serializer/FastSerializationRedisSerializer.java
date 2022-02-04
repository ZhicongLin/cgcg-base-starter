package org.cgcg.redis.core.serializer;

import org.nustaq.serialization.FSTConfiguration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * @program: cgcg-base-starter
 * @description: Fast Serialization
 * @author: zhicong.lin
 * @create: 2022-02-02 22:28
 **/
public class FastSerializationRedisSerializer implements RedisSerializer<Object> {

    private final FSTConfiguration tConf = FSTConfiguration.createDefaultConfiguration();

    @Override
    public byte[] serialize(Object o) throws SerializationException {
        if (o == null) {
            return new byte[0];
        }
        return tConf.asByteArray(o);
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return tConf.asObject(bytes);
    }
}
