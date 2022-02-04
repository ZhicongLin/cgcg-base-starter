package org.cgcg.redis.core.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * 序列化工具
 *
 * @author zhicong.lin
 */
@Slf4j
public class KryoRedisSerializer implements RedisSerializer<Object> {
    /**
     * 由于kryo不是线程安全的，所以每个线程都使用独立的kryo
     */
    final ThreadLocal<Kryo> kryoLocal = ThreadLocal.withInitial(() -> {
        final Kryo kryo = new Kryo();
        // 支持对象循环引用（否则会栈溢出）
        // 默认也是true，这里设置是为了提醒维护的时候别设置false
        kryo.setReferences(true);
        //不强制要求注册类（注册行为无法保证多个 JVM 内同一个类的注册编号相同；而且业务系统中大量的 Class 也难以一一注册）
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public byte[] serialize(Object t) throws SerializationException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final Output output = new Output(byteArrayOutputStream);
        kryoLocal.get().writeClassAndObject(output, t);
        output.flush();
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        final Input input = new Input(byteArrayInputStream);
        return kryoLocal.get().readClassAndObject(input);
    }

}