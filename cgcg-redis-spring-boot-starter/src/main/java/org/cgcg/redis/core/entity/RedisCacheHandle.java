package org.cgcg.redis.core.entity;

import java.util.concurrent.TimeUnit;

import org.cgcg.redis.core.enums.RedisExecuteType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Description:
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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RedisCacheHandle {

    String cache;

    String key;

    long expire;

    TimeUnit timeUnit;

    RedisExecuteType type;

    boolean lock;
}