package org.cgcg.redis.core.annotation;

import org.cgcg.redis.core.entity.RedisCacheResult;
import org.cgcg.redis.core.entity.RedisMethodSignature;
import org.cgcg.redis.core.enums.RedisExecuteType;
import org.cgcg.redis.core.interceptor.RedisCacheExecutor;
import org.cgcg.redis.core.interceptor.RedisCacheModifyExecutor;
import org.springframework.data.redis.core.RedisTemplate;

import lombok.Getter;

/**
 * Description:
 *
 * @author linzc
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期        修改内容
 * 2020/6/22.1    linzc       2020/6/22     Create
 * </pre>
 * @date 2020/6/22
 */
@Getter
public abstract class RedisCacheDispatcher {
    private final RedisMethodSignature signature;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisCache redisCacheAnn;
    private final Object result;
    private RedisCacheResult cacheResult;

    /**
     * 构造
     * @param signature
     * @param redisTemplate
     * @throws Throwable
     */
    public RedisCacheDispatcher(RedisMethodSignature signature, RedisTemplate<String, Object> redisTemplate) throws Throwable {
        this.redisTemplate = redisTemplate;
        this.signature = signature;
        this.redisCacheAnn = signature.getRedisCache();
        this.before();
        if (cacheResult.isExecuteMethod()) {
            this.result = this.execute();
            this.after();
        } else {
            this.result = this.cacheResult.getResult();
        }
    }


    /**
     * 方法执行前查询缓存，并判断是否需要执行方法
     */
    private void before() {
        if (RedisExecuteType.SELECT.equals(this.redisCacheAnn.type())) {
            this.cacheResult = RedisCacheExecutor.beforeMethodInvoke(this.redisTemplate, this.signature);
        } else {
            this.cacheResult = RedisCacheModifyExecutor.beforeMethodInvoke(this.redisTemplate, this.signature);
        }
    }

    /**
     * 执行方法
     * @return
     * @throws Throwable
     */
    public abstract Object execute() throws Throwable;

    /**
     * 方法执行后操作缓存
     */
    private void after() {
        if (RedisExecuteType.SELECT.equals(this.redisCacheAnn.type())) {
            RedisCacheExecutor.afterMethodInvoke(this.redisTemplate, this.result, this.signature);
        } else {
            RedisCacheModifyExecutor.afterMethodInvoke(this.redisTemplate, this.result, this.signature);
        }
    }

}