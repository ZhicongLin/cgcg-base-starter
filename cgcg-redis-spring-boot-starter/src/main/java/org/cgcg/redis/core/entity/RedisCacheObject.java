package org.cgcg.redis.core.entity;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.cgcg.redis.core.annotation.RedisCache;
import org.cgcg.redis.core.annotation.RedisNameSpace;
import org.cgcg.redis.core.constant.Constant;
import org.cgcg.redis.core.enums.LockUnit;
import org.cgcg.redis.core.enums.RedisEnum;
import org.cgcg.redis.core.enums.RedisTimeUnit;
import org.cgcg.redis.core.exception.RedisExpireException;
import org.cgcg.redis.core.util.SpelUtils;
import org.springframework.core.env.Environment;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * RedisCahce缓存对象 .
 *
 * @author zhicong.lin
 * @date 2019/6/20
 */
@Slf4j
@Setter
@Getter
public class RedisCacheObject {
    private static final long START_DEFAULT_TIME = 3600L;
    private static final long DEFAULT_TIME = 7201L;
    private static String timeRegex = "^\\d+$";
    private Environment env;
    private ProceedingJoinPoint proceedingJoinPoint;
    private Signature signature;
    private Method method;
    private RedisCache redisAnn;
    private String cacheName;
    private String cacheKey;
    private long time;
    private TimeUnit unit;
    private CacheNameObject cno;
    private LockUnit lock;

    public RedisCacheObject(ProceedingJoinPoint proceedingJoinPoint, RedisCache redisAnn, Environment env) {
        this.proceedingJoinPoint = proceedingJoinPoint;
        this.redisAnn = redisAnn;
        this.time = RandomUtils.nextLong(START_DEFAULT_TIME, DEFAULT_TIME);
        this.signature = proceedingJoinPoint.getSignature();
        this.env = env;
        this.lock = redisAnn.lock();
        this.initProperty();
    }

    private void initProperty() {
        this.generationNameSpace();
        this.generationCacheName();
        this.generationCacheKey();
        this.generationExpireTime();
    }

    private void generationExpireTime() {
        this.setExpireTime(redisAnn.expire());
        final RedisTimeUnit timeUnit = redisAnn.timeUnit();
        if (this.unit != null && timeUnit.equals(RedisTimeUnit.NULL)) {
            return;
        }
        this.unit = timeUnit.getTimeUnit();
    }

    private void generationCacheKey() {
        final String key = redisAnn.key();
        if (StringUtils.isBlank(key)) {
            cacheKey = JSON.toJSONString(proceedingJoinPoint.getArgs());
        } else if (key.contains("#")) {
            MethodSignature methodSignature = (MethodSignature) signature;
            cacheKey = SpelUtils.parse(proceedingJoinPoint.getTarget(), key, methodSignature.getMethod(), proceedingJoinPoint.getArgs());
        } else {
            cacheKey = key;
        }
    }

    /**
     * 判断时间是纯数字还是表达式，如果纯数字，则直接返回long格式；
     * 如果表达式则去配置文件获取时间
     *
     * @auth zhicong.lin
     * @date 2019/6/21
     */
    private void setExpireTime(String[] expire) {
        if (expire == null || expire.length == 0) {
            return;
        }
        if (expire.length == 1) {
            this.time = this.getExpireLong(expire[0]);
        } else if (expire.length == 2) {
            final long expireLong1 = this.getExpireLong(expire[0]);
            final long expireLong2 = this.getExpireLong(expire[1]);
            if (expireLong1 > expireLong2) {
                this.time = RandomUtils.nextLong(expireLong2, expireLong1 + 1);
            } else {
                this.time = RandomUtils.nextLong(expireLong1, expireLong2 + 1);
            }
        } else {
            final int index = RandomUtils.nextInt(0, expire.length);
            this.time = this.getExpireLong(expire[index]);
        }
    }

    /**
     * 判断时间是纯数字还是表达式，如果纯数字，则直接返回long格式；
     * 如果表达式则去配置文件获取时间
     *
     * @param expire 过期时间或者过期时间对应配置的key
     * @return long time
     */
    private long getExpireLong(String expire) {
        if (StringUtils.isNotBlank(expire)) {
            final boolean matches = expire.matches(timeRegex);
            if (matches) {
                return Long.parseLong(expire);
            } else {
                final String property = env.getProperty(expire);
                if (property != null && property.matches(timeRegex)) {
                    return Long.parseLong(property);
                }
            }
            final RedisExpireException expireException = new RedisExpireException(expire);
            log.error(expireException.getMessage(), expireException);
            throw expireException;
        }
        return 0;
    }

    /**
     * 是否注解RedisNameSpace的内容？
     * 有cacheName加上当前cacheName，修改默认时间和单位
     * 没有的话，直接返回
     *
     * @auth zhicong.lin
     * @date 2019/6/21
     */
    private void generationNameSpace() {
        final Class<?> declaringType = signature.getDeclaringType();
        final RedisNameSpace annotation = declaringType.getAnnotation(RedisNameSpace.class);
        if (annotation == null) {
            return;
        }
        this.cacheName = annotation.cache();
        this.setExpireTime(annotation.expire());
        this.unit = annotation.unit().getTimeUnit();
    }

    /**
     * 生成缓存名称 + “::select” 格式的cacheName
     *
     * @auth zhicong.lin
     * @date 2019/6/21
     */
    private void generationCacheName() {
        this.createCacheName();
        if (StringUtils.isBlank(this.cacheName)) {
            this.cacheName = cno.getName();
        } else {
            if (StringUtils.isNotBlank(cno.getName())) {
                this.cacheName += (":" + cno.getName());
            }
        }
        if (StringUtils.isBlank(this.cacheName)) {
            this.cacheName = signature.getDeclaringType().getName() + "." + signature.getName();
        }
        cno.setName(this.cacheName);
    }

    private String clearCacheName(String cacheName, final String type) {
        switch (type) {
            case Constant.SEL:
                return cacheName.replace(Constant.SEL, "");
            case Constant.UPD:
                return cacheName.replace(Constant.UPD, "");
            case Constant.DEL:
                return cacheName.replace(Constant.DEL, "");
            default:
                return "";
        }
    }

    private void createCacheName() {
        RedisEnum suffix;
        String name;
        boolean isLockRedis = false;
        final String cache = redisAnn.cache();
        if (redisAnn.cache().endsWith(Constant.UPD) || RedisEnum.UPD.equals(redisAnn.type())) {
            if (this.lock.equals(LockUnit.NULL)) {
                isLockRedis = true;
            }
            suffix = RedisEnum.UPD;
            name = clearCacheName(cache, Constant.UPD);
        } else if (redisAnn.cache().endsWith(Constant.DEL) || RedisEnum.DEL.equals(redisAnn.type())) {
            if (this.lock.equals(LockUnit.NULL)) {
                isLockRedis = true;
            }
            suffix = RedisEnum.DEL;
            name = clearCacheName(cache, Constant.DEL);
        } else if (redisAnn.cache().endsWith(Constant.FLUSH) || RedisEnum.FLUSH.equals(redisAnn.type())) {
            if (this.lock.equals(LockUnit.NULL)) {
                isLockRedis = true;
            }
            suffix = RedisEnum.FLUSH;
            name = clearCacheName(cache, Constant.FLUSH);
        } else {
            suffix = RedisEnum.SEL;
            name = clearCacheName(cache, Constant.SEL);
        }
        if (lock.equals(LockUnit.LOCK)) {
            isLockRedis = true;
        } else if (lock.equals(LockUnit.UNLOCK)) {
            isLockRedis = false;
        }
        this.cno = new CacheNameObject(name, suffix, isLockRedis);
    }


}
