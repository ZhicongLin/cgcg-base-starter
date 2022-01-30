package org.cgcg.redis.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

/**
 * .
 *
 * @author zhicong.lin
 * @date 2019/6/21
 */
@Getter
@AllArgsConstructor
public enum RedisTimeUnit {

    /**
     * 秒
     */
    NULL(TimeUnit.SECONDS),

    /**
     * 秒
     */
    SECONDS(TimeUnit.SECONDS),

    /**
     * 分钟
     */
    MINUTES(TimeUnit.MINUTES),

    /**
     * 小时
     */
    HOURS(TimeUnit.HOURS),

    /**
     * 天
     */
    DAYS(TimeUnit.DAYS);
    private final TimeUnit timeUnit;

}
