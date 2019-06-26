package org.cgcg.redis.core;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.cgcg.redis.core.annotation.RedisCache;
import org.cgcg.redis.core.entity.*;
import org.cgcg.redis.core.enums.RedisEnum;
import org.springframework.core.env.Environment;
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
            final Object cacheResult = redisHelper.hget(rco.getCacheName(), rco.getCacheKey());
            if (cacheResult != null) {
                return cacheResult;
            }
        }
        //执行方法
        final Object proceed = pjp.proceed();

        if (RedisEnum.DEL.equals(cno.getSuffix())) {
            //删除的注解，在方法执行完成后，执行删除
            this.deleteCache(cno, cacheKey);
        } else if (RedisEnum.FLUSH.equals(cno.getSuffix())) {
            //清空缓存数据
            this.flushCache(cno);
        } else {
            //其他的注解，则缓存数据
            this.cacheMethodValue(rco, cno.getName(), cacheKey, proceed);
        }
        return proceed;
    }

    /**
     * 清空当前缓存的所有数据
     * @auth zhicong.lin
     * @date 2019/6/26
     */
    private void flushCache(CacheNameObject cno) {
        //清空缓存数据
        if (cno.isLock()) {
            RedisTask.executeAsync(this.redisHelper, cno.getName(), new Callback() {
                @Override
                public void execute() {
                    redisHelper.del(cno.getName());
                }
            });
        } else {
            redisHelper.del(cno.getName());
        }
    }

    /**
     * 删除缓存结果数据
     * @auth zhicong.lin
     * @date 2019/6/26
     */
    private void deleteCache(CacheNameObject cno, String cacheKey) {
        final String lockKey = cno.getName() + cacheKey;
        //删除的注解，在方法执行完成后，执行删除
        if (cno.isLock()) {
            RedisTask.executeAsync(this.redisHelper, lockKey, new Callback() {
                @Override
                public void execute() {
                    redisHelper.remove(cno.getName(), cacheKey);
                }
            });
        } else {
            redisHelper.remove(cno.getName(), cacheKey);
        }
    }

    /**
     * 缓存执行结果数据
     * @auth zhicong.lin
     * @date 2019/6/26
     */
    private void cacheMethodValue(RedisCacheObject rco, String cacheName, String cacheKey, Object proceed) {
        if (rco.getCno() != null && rco.getCno().isLock()) {
            final String lockKey = cacheName + cacheKey;
            RedisTask.executeAsync(this.redisHelper, lockKey, new Callback() {
                @Override
                public void execute() {
                    redisHelper.hset(cacheName, cacheKey, proceed, rco.getTime(), rco.getUnit());
                }
            });
        } else {
            redisHelper.hset(cacheName, cacheKey, proceed, rco.getTime(), rco.getUnit());
        }
    }

}
