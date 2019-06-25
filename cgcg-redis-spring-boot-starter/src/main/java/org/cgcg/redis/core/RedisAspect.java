package org.cgcg.redis.core;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.cgcg.redis.core.annotation.RedisCache;
import org.cgcg.redis.core.entity.CacheNameObject;
import org.cgcg.redis.core.entity.RedisCacheObject;
import org.cgcg.redis.core.entity.RedisHelper;
import org.cgcg.redis.core.enums.RedisEnum;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * .
 *
 * @author zhicong.lin
 * @date 2019/6/20
 */
@Aspect
@Component
public class RedisAspect {
    @Resource
    @Qualifier("redisCacheTemplate")
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private Environment env;
    @Resource
    private RedisHelper redisHelper;

    @Around("@annotation(redisCache)")
    public Object processor(ProceedingJoinPoint pjp, RedisCache redisCache) throws Throwable {
        final RedisCacheObject rco = new RedisCacheObject(pjp, redisCache, env);
        final CacheNameObject cno = rco.getCno();
        final String cacheKey = rco.getCacheKey();
        if (!RedisEnum.UPD.equals(cno.getSuffix()) && !RedisEnum.DEL.equals(cno.getSuffix()) && !RedisEnum.FLUSH.equals(cno.getSuffix())) {
            //查询的注解，先去查询缓存
            final Object cacheResult = redisTemplate.opsForHash().get(cno.getName(), cacheKey);
            if (cacheResult != null) {
                return cacheResult;
            }
        }
        //执行方法
        final Object proceed = pjp.proceed();

        if (RedisEnum.DEL.equals(cno.getSuffix())) {
            //删除的注解，在方法执行完成后，执行删除
            redisHelper.remove(cno.getName(), cacheKey);
        } else if (RedisEnum.FLUSH.equals(cno.getSuffix())) {
            //清空缓存数据
            redisHelper.del(cno.getName());
        } else {
            //其他的注解，则缓存数据
            this.cacheMethodValue(rco, cno.getName(), cacheKey, proceed);
        }
        return proceed;
    }

    private void cacheMethodValue(RedisCacheObject rco, String cacheName, String cacheKey, Object proceed) {
        if (rco.getCno().isLock()) {
            final String key = cacheName + cacheKey;
            boolean lock = false;
            try {
                lock = this.redisHelper.lock(key);
                if (lock) {
                    redisHelper.hset(cacheName, cacheKey, proceed, rco.getTime(), rco.getUnit());
                }
            } finally {
                if (lock) {
                    this.redisHelper.delete(key);
                }
            }
        } else {
            redisTemplate.opsForHash().put(cacheName, cacheKey, proceed);
            if (rco.getTime() > 0) {
                redisTemplate.expire(cacheName, rco.getTime(), rco.getUnit());
            }
        }

    }

}
