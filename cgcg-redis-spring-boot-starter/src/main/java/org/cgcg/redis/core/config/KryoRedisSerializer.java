package org.cgcg.redis.core.config;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;
import org.cgcg.redis.core.RedisManager;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.util.Arrays;

@Slf4j
public class KryoRedisSerializer<T> implements RedisSerializer<T> {
    // 由于kryo不是线程安全的，所以每个线程都使用独立的kryo
    final ThreadLocal<Kryo> kryoLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        kryo.setReferences(true);
        return kryo;
    });

    @Override
    public byte[] serialize(Object t) throws SerializationException {
        byte[] buffer = new byte[2048];
        Output output = new Output(buffer);
        kryoLocal.get().writeClassAndObject(output, t);
        return output.toBytes();
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }

        Kryo kryo = kryoLocal.get();

        try (Input input = new Input(bytes)) {
            final Object result = kryo.readClassAndObject(input);
            if (result != null) {
                @SuppressWarnings("unchecked") final T t = (T) result;
                return t;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

}