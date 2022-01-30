package org.cgcg.redis.core.annotation;

import org.cgcg.redis.core.mq.RedisMqBeanDefinitionRegistrar;
import org.cgcg.redis.core.mq.RedisMqConfiguration;
import org.cgcg.redis.core.mq.RedisMqListenerRegistrar;
import org.cgcg.redis.core.mq.RedisMqProducerAspect;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Redis缓存注解.
 *
 * @author zhicong.lin
 * @date 2019/6/20
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import({
        RedisMqBeanDefinitionRegistrar.class,
        RedisMqConfiguration.class,
        RedisMqListenerRegistrar.class,
        RedisMqProducerAspect.class
})
public @interface EnableRedisMQ {

    String[] packages() default {};

}
