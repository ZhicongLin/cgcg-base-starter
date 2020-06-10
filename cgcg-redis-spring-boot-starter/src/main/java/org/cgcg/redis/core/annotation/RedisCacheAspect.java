package org.cgcg.redis.core.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Description:
 *
 * @author linzc
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本           修改人       修改日期         修改内容
 * 2020/6/10.1       linzc    2020/6/10           Create
 * </pre>
 * @date 2020/6/10
 */
@Aspect
@Component
public class RedisCacheAspect {

    public Object round(ProceedingJoinPoint proceedingJoinPoint, RedisCache redisCache) throws Throwable {
        final Object[] args = proceedingJoinPoint.getArgs();
        final Object proceed = proceedingJoinPoint.proceed();
        return proceed;
    }
}