package org.cgcg.redis.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.cgcg.redis.core.entity.RedisCacheHandle;
import org.cgcg.redis.core.entity.RedisCacheResult;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ClassUtils;

import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class RedisCacheInterceptor implements MethodInterceptor {

    private static final String TIME_REGEX = "^\\d+$";
    private static final long DEFAULT_EXPIRE = 7200L;
    private final RedisTemplate<String, Object> redisTemplate;
    private final Environment environment;
    private boolean disable;

    public RedisCacheInterceptor(RedisTemplate<String, Object> redisTemplate, Environment environment) {
        this.redisTemplate = redisTemplate;
        this.environment = environment;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Class<?> targetClass = methodInvocation.getThis() != null ? AopUtils.getTargetClass(methodInvocation.getThis())
                : null;
        Method specificMethod = ClassUtils.getMostSpecificMethod(methodInvocation.getMethod(), targetClass);
        final Method method = BridgeMethodResolver.findBridgedMethod(specificMethod);
        final RedisCache redisCacheAnnotation = getAnnotation(method, RedisCache.class);
        if (disable || redisCacheAnnotation == null) {
            return methodInvocation.proceed();
        }
        return handleRedisCache(methodInvocation, redisCacheAnnotation);
    }

    private Object handleRedisCache(MethodInvocation methodInvocation, RedisCache redisCacheAnnotation) throws Throwable {
        final RedisCacheHandle redisCacheHandle = getRedisCacheHandle(methodInvocation, redisCacheAnnotation);
        //方法执行前查询缓存，并判断是否需要执行方法
        final RedisCacheResult cacheResult = RedisCacheExecutor.beforeMethodInvoke(redisTemplate, redisCacheHandle, methodInvocation);
        Object result = cacheResult.getResult();
        if (cacheResult.isInvoke()) {
            result = methodInvocation.proceed();
            //方法执行后操作缓存
            RedisCacheExecutor.afterMethodInvoke(redisTemplate, result, redisCacheHandle, methodInvocation);
        }
        return result;
    }

    private RedisCacheHandle getRedisCacheHandle(MethodInvocation methodInvocation, RedisCache redisCache) {
        final RedisCacheHandle redisCacheHandle = generationNameSpace(methodInvocation);
        final long expireTime = getExpireTime(redisCache.expire());
        if (redisCacheHandle == null) {
            return new RedisCacheHandle(redisCache.cache(), redisCache.key(), expireTime, redisCache.timeUnit(), redisCache.type(), redisCache.lock());
        } else {
            if (StringUtils.isNotBlank(redisCache.cache())) {
                redisCacheHandle.setCache(redisCache.cache());
            }
            if (StringUtils.isNotBlank(redisCache.expire())) {
                redisCacheHandle.setExpire(expireTime);
                redisCacheHandle.setTimeUnit(redisCache.timeUnit());
            }
            redisCacheHandle.setKey(redisCache.key());
            redisCacheHandle.setType(redisCache.type());
            redisCacheHandle.setLock(redisCache.lock());
        }
        return redisCacheHandle;
    }

    private long getExpireTime(String expire) {
        if (StringUtils.isNotBlank(expire)) {
            final boolean matches = expire.matches(TIME_REGEX);
            if (matches) {
                return Long.parseLong(expire);
            } else if (StringUtils.isNotBlank(expire)) {
                final String property = environment.getProperty(expire);
                if (property != null && property.matches(TIME_REGEX)) {
                    return Long.parseLong(property);
                }
            }
        }
        return DEFAULT_EXPIRE;
    }

    /**
     * 是否注解RedisNameSpace的内容？
     * 有cacheName加上当前cacheName，修改默认时间和单位
     * 没有的话，直接返回
     *
     * @auth zhicong.lin
     * @date 2019/6/21
     */
    private RedisCacheHandle generationNameSpace(MethodInvocation methodInvocation) {
        final Class<?> declaringType = methodInvocation.getMethod().getDeclaringClass();
        final RedisNameSpace nameSpace = declaringType.getAnnotation(RedisNameSpace.class);
        if (nameSpace == null) {
            return null;
        }
        return new RedisCacheHandle(nameSpace.cache(), "", this.getExpireTime(nameSpace.expire()), nameSpace.unit(), null, false);
    }

    private <T extends Annotation> T getAnnotation(Method method, Class<T> clazz) {
        return method == null ? null : method.getAnnotation(clazz);
    }

}