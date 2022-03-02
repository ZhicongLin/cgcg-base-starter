package com.cgcg.base.util;

import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 集合工具类
 * </p>
 * @author zhicong.lin
 * @since 2022/2/22 17:49
 */
@Accessors(chain = true)
@NoArgsConstructor
public final class Collections {

    /**
     * 创建一个ArrayList
     * @return java.util.List<E>
     * @author zhicong.lin
     * @date 2022/2/22 17:59
     */
    public static <E> List<E> arrayList() {
        return new ArrayList<>();
    }

    /**
     * 创建一个ArrayList
     * @return java.util.List<E>
     * @author zhicong.lin
     * @date 2022/2/22 17:59
     */
    public static <E> List<E> arrayList(Collection<E> collection) {
        return new ArrayList<>(collection);
    }

    /**
     * 创建一个容量为4的HashMap，并存放KV
     * @param key 键
     * @param value 值
     * @return java.util.Map<K, V>
     * @author zhicong.lin
     * @date 2022/2/22 17:58
     */
    public static <K, V> Map<K, V> singleHashMap(K key, V value) {
        final Map<K, V> map = Maps.newHashMap(1);
        map.put(key, value);
        return map;
    }
}
