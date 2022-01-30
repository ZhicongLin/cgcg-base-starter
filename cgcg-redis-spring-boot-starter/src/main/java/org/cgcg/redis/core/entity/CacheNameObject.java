package org.cgcg.redis.core.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.cgcg.redis.core.enums.RedisEnum;

/**
 * @author zhicong.lin
 */
@AllArgsConstructor
@Getter
@Setter
public class CacheNameObject {
    String name;
    RedisEnum suffix;
    boolean lock;
}
