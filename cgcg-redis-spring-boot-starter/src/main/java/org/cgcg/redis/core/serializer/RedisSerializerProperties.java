package org.cgcg.redis.core.serializer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @program: cgcg-base-starter
 * @description: 序列化类型配置
 * @author: zhicong.lin
 * @create: 2022-02-02 21:33
 **/
@Setter
@Getter
@Component
@ConfigurationProperties("spring.redis")
public class RedisSerializerProperties {

    private SerialType serial = SerialType.FST;
}
