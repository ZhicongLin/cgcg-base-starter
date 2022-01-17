package org.cgcg.redis.core;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.cgcg.redis.core.annotation.RedisCache;
import org.cgcg.redis.core.entity.CacheNameObject;
import org.cgcg.redis.core.entity.RedisCacheObject;
import org.cgcg.redis.core.entity.RedisHelper;
import org.cgcg.redis.core.entity.RedisTask;
import org.cgcg.redis.core.enums.RedisEnum;
import org.cgcg.redis.core.penetrate.RedisPenetrate;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

/**
 * Redis缓存处理Aop.
 *
 * @author zhicong.lin
 * @date 2019/6/20
 */
@Order(-1)
@Aspect
@Component
@Slf4j
public class RedisAspect {
    @Resource
    private Environment env;
    @Resource
    private RedisHelper redisHelper;

    @Around("@annotation(redisCache)")
    public Object processor(ProceedingJoinPoint pjp, RedisCache redisCache) throws Throwable {
        if (!RedisManager.online) {
            //缓存不在线则不缓存数据
            return pjp.proceed();
        }
        final RedisCacheObject rco = new RedisCacheObject(pjp, redisCache, env);
        final CacheNameObject cno = rco.getCno();
        final String cacheKey = rco.getCacheKey();
        boolean selectCache = false;
        final String key = cno.getName() + ":" + cacheKey;
        if (!RedisEnum.UPD.equals(cno.getSuffix()) && !RedisEnum.DEL.equals(cno.getSuffix()) && !RedisEnum.FLUSH.equals(cno.getSuffix())) {
            //查询的注解，先去查询缓存
            final Object cacheResult = redisHelper.get(key);
            if (cacheResult != null) {
                if (RedisPenetrate.PENETRATE_VALUE.equals(cacheResult.toString())) {
                    log.warn("[key={}]缓存穿透超过3次，1分钟内由缓存直接返回null值", key);
                    //防止缓存穿透，直接返回null
                    return null;
                }
                log.info("Hit Redis Cache[{}]", key);
                return cacheResult;
            } else {
                selectCache = true;
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
            log.info("Serializer 2 Redis Cache[{}]", key);
        }
        if (selectCache && proceed == null) {
            RedisPenetrate.setPenetrate(key);
        }
        return proceed;
    }

    /**
     * 清空当前缓存的所有数据
     *
     * @auth zhicong.lin
     * @date 2019/6/26
     */
    private void flushCache(CacheNameObject cno) {
        final Set<String> keys = redisHelper.keys(cno.getName() + ":");
        //清空缓存数据
        if (cno.isLock()) {
            RedisTask.executeAsync(this.redisHelper, cno.getName(), () -> redisHelper.del(keys));
        } else {
            redisHelper.del(keys);
        }
    }

    /**
     * 删除缓存结果数据
     *
     * @auth zhicong.lin
     * @date 2019/6/26
     */
    private void deleteCache(CacheNameObject cno, String cacheKey) {
        final String key = cno.getName() + ":" + cacheKey;
        //删除的注解，在方法执行完成后，执行删除
        if (cno.isLock()) {
            RedisTask.executeAsync(this.redisHelper, key, () -> redisHelper.del(key));
        } else {
            redisHelper.del(key);
        }
    }

    /**
     * 缓存执行结果数据
     *
     * @auth zhicong.lin
     * @date 2019/6/26
     */
    private void cacheMethodValue(RedisCacheObject rco, String cacheName, String cacheKey, Object proceed) {
        final String key = cacheName + ":" + cacheKey;
        if (rco.getCno() != null && rco.getCno().isLock()) {
            RedisTask.executeAsync(this.redisHelper, key, () -> redisHelper.set(key, proceed, rco.getTime(), rco.getUnit()));
        } else {
            redisHelper.set(key, proceed, rco.getTime(), rco.getUnit());
        }
    }

}
