package org.cgcg.redis.core.entity;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.cgcg.redis.core.RedisHelper;
import org.cgcg.redis.core.annotation.RedisCache;
import org.cgcg.redis.core.annotation.RedisNameSpace;
import org.cgcg.redis.core.util.SpelUtils;
import org.springframework.core.env.Environment;

import com.alibaba.fastjson.JSON;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: 缓存方法对象
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
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisCacheMethod {

    private static final String KEY_TEMP = "chk::%s::%s";

    private Method method;
    private Object[] args;
    private Class<?> returnType;
    private Class<?> beanType;
    private long expire;
    private String cache;
    private TimeUnit timeUnit;
    private String key;
    private RedisCache redisCache;
    private Environment environment;

    /**
     * 构造
     *
     * @param method
     * @param args
     * @param environment
     */
    public RedisCacheMethod(Method method, Object[] args, Environment environment, RedisCache redisCache) {
        this.method = method;
        this.args = args;
        this.returnType = method.getReturnType();
        this.beanType = method.getDeclaringClass();
        this.redisCache = redisCache;
        this.environment = environment;
        this.buildMethod();
        this.buildKey();
    }

    /**
     * 初始化相关参数信息
     */
    private void buildMethod() {
        final RedisNameSpace rns = this.beanType.getAnnotation(RedisNameSpace.class);
        if (rns != null) {
            //RedisNameSpace 命名空间组名 和 缓存时间
            this.cache = rns.cache();
            this.buildExpire(rns.expire(), rns.unit());
        }

        if (StringUtils.isNoneBlank(redisCache.cache())) {
            //redis cache 组名信息
            this.cache = redisCache.cache();
        }

        if (StringUtils.isBlank(this.cache)) {
            //组名信息为空，则默认 （类名+方法名）
            this.cache = this.beanType.getName() + "." + this.method.getName();
        }

        this.buildExpire(redisCache.expire(), redisCache.timeUnit());

        if (this.expire <= 0) {
            //时间为空，默认两个小时
            //缓存加上过期时间，防止垃圾缓存堆积
            this.expire = RedisHelper.DEFAULT_EXPIRE;
            this.timeUnit = RedisHelper.DEFAULT_TIME_UNIT;
        }
    }

    /**
     * 加入缓存时间
     *
     * @param expire
     * @param timeUnit
     */
    private void buildExpire(String expire, TimeUnit timeUnit) {
        if (StringUtils.isNoneBlank(expire)) {
            this.expire = SpelUtils.getExpireTime(expire, environment);
            this.timeUnit = timeUnit;
        }
    }

    /**
     * 获取缓存key
     *
     * @return
     */
    private void buildKey() {
        String cacheKey = redisCache.key();
        if (StringUtils.isBlank(cacheKey)) {
            cacheKey = JSON.toJSONString(this.args);
        } else if (cacheKey.contains("#")) {
            cacheKey = SpelUtils.parse(this.beanType.getName(), cacheKey, this.method, this.args);
        }
        this.key = String.format(KEY_TEMP, this.getCache(), cacheKey);
    }
}