package org.cgcg.redis.core.annotation;

import org.cgcg.redis.core.mq.RedisMQBeanDefinitionRegistrar;
import org.cgcg.redis.core.mq.RedisMQProducerAspect;
import org.cgcg.redis.core.mq.RedisMqConfiguration;
import org.cgcg.redis.core.mq.RedisMqListenerRegistrar;
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
        RedisMQBeanDefinitionRegistrar.class,
        RedisMqConfiguration.class,
        RedisMqListenerRegistrar.class,
        RedisMQProducerAspect.class
})
public @interface EnableRedisMQ {

    String[] packages() default {};

}
