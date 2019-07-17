package org.cgcg.redis.core.annotation;

import org.cgcg.redis.core.RedisAspect;
import org.cgcg.redis.core.RedisManager;
import org.cgcg.redis.core.context.SpringCacheHolder;
import org.cgcg.redis.core.entity.RedisHelper;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({SpringCacheHolder.class ,RedisManager.class, RedisAspect.class, RedisHelper.class})
@Deprecated
public @interface EnableCgCgRedis {

    String value() default "";
}
