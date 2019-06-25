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
    NULL(TimeUnit.SECONDS),
    SECONDS(TimeUnit.SECONDS),
    MINUTES(TimeUnit.MINUTES),
    HOURS(TimeUnit.HOURS),
    DAYS(TimeUnit.DAYS)
    ;
    private TimeUnit timeUnit;

}
