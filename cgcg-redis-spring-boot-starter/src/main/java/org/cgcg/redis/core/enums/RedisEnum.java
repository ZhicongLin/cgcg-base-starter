package org.cgcg.redis.core.enums;

/**
 * .
 *
 * @author zhicong.lin
 * @date 2019/6/20
 */
public enum RedisEnum {

    /**
     * 查询
     */
    SEL,

    /**
     * 删除缓存
     */
    DEL,

    /**
     * 修改缓存
     */
    UPD,

    /**
     * 清空同一命名空间下的缓存
     */
    FLUSH,

    /**
     * 为空时使用默认
     */
    NULL;

}
