package org.cgcg.redis.core.interceptor;

import java.lang.reflect.Method;

import javax.annotation.Resource;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.cgcg.redis.core.annotation.RedisCache;
import org.cgcg.redis.core.annotation.RedisLock;
import org.cgcg.redis.core.annotation.RedisNameSpace;
import org.cgcg.redis.core.entity.RedisCacheHandle;
import org.cgcg.redis.core.util.SpelUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ClassUtils;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;

/**
 * Description: 缓存拦截器模版
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
public abstract class AbstractRedisCacheInterceptor implements MethodInterceptor {

    private static final String TIME_REGEX = "^\\d+$";
    private static final long DEFAULT_EXPIRE = 7200L;
    @Resource
    protected RedisTemplate<String, Object> redisTemplate;
    protected Environment environment;
    private boolean disable;

    public AbstractRedisCacheInterceptor() {
    }

    public AbstractRedisCacheInterceptor(RedisTemplate<String, Object> redisTemplate, Environment environment) {
        this.redisTemplate = redisTemplate;
        this.environment = environment;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Class<?> targetClass = methodInvocation.getThis() != null ? AopUtils.getTargetClass(methodInvocation.getThis())
                : null;
        Method specificMethod = ClassUtils.getMostSpecificMethod(methodInvocation.getMethod(), targetClass);
        final Method method = BridgeMethodResolver.findBridgedMethod(specificMethod);
        final RedisCache redisCacheAnnotation = method.getAnnotation(RedisCache.class);
        final RedisLock redisLockAnnotation = method.getAnnotation(RedisLock.class);
        if (disable || (redisCacheAnnotation == null && redisLockAnnotation == null)) {
            return methodInvocation.proceed();
        }
        if (redisCacheAnnotation != null) {
            return invoke(methodInvocation, redisCacheAnnotation);
        }
        return invoke(methodInvocation, redisLockAnnotation);
    }
    /**
     * 执行缓存操作
     *
     * @param methodInvocation
     * @param redisLockAnnotation
     * @return
     * @throws Throwable
     */
    public abstract Object invoke(MethodInvocation methodInvocation, RedisLock redisLockAnnotation) throws Throwable;

    /**
     * 执行缓存操作
     *
     * @param methodInvocation
     * @param redisCacheAnnotation
     * @return
     * @throws Throwable
     */
    public abstract Object invoke(MethodInvocation methodInvocation, RedisCache redisCacheAnnotation) throws Throwable;

    protected RedisCacheHandle getRedisCacheHandle(MethodInvocation methodInvocation, RedisCache redisCache) {
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

    protected long getExpireTime(String expire) {
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
    protected RedisCacheHandle generationNameSpace(MethodInvocation methodInvocation) {
        final Class<?> declaringType = methodInvocation.getMethod().getDeclaringClass();
        final RedisNameSpace nameSpace = declaringType.getAnnotation(RedisNameSpace.class);
        if (nameSpace == null) {
            return null;
        }
        return new RedisCacheHandle(nameSpace.cache(), "", this.getExpireTime(nameSpace.expire()), nameSpace.unit(), null, false);
    }
    /**
     * 获取缓存锁key
     *
     * @param method
     * @param args
     * @return
     */
    protected static String getLockKey(RedisLock redisLock, Method method, Object[] args) {
        String lockKey = redisLock.key();
        if (StringUtils.isBlank(lockKey)) {
            lockKey = JSON.toJSONString(args);
        } else if (lockKey.contains("#")) {
            lockKey = SpelUtils.parse(method.getDeclaringClass().getName(), lockKey, method, args);
        }
        return method.getDeclaringClass().getName() + "." + method.getName() + "::" + lockKey;
    }

}