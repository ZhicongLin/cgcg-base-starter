package org.cgcg.redis.mybatis.annotation;

import org.cgcg.redis.mybatis.CgBeanHolder;
import org.cgcg.redis.mybatis.ExceptionHandlerAdvice;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * .
 *
 * @author zhicong.lin
 * @date 2019/6/24
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({CgBeanHolder.class, ExceptionHandlerAdvice.class})
public @interface EnableMyBatisCache {
}
