package org.cgcg.redis.core.entity;

import lombok.Data;

/**
 * Description: 缓存结果对象
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
@Data
public class RedisCacheResult {
    private Object result;
    private boolean invoke;
}