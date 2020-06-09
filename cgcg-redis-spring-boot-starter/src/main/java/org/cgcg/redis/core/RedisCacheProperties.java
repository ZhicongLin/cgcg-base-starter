package org.cgcg.redis.core;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * Description: RedisCache缓存配置
 *
 * @author linzc
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本           修改人       修改日期         修改内容
 * 2020/6/5.1       linzc    2020/6/5           Create
 * </pre>
 * @date 2020/6/5
 */
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "cgcg.redis")
public class RedisCacheProperties {

    private boolean disable;
}