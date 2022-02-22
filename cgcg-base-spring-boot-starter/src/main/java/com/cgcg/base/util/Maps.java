package com.cgcg.base.util;

import java.util.HashMap;

/**
 * <p>
 * Map工具类
 * </p>
 * @author zhicong.lin
 * @since 2022/2/22 16:59
 */
public final class Maps {
    private static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
    private static final int MAXIMUM_CAPACITY = 1 << 30;
    private static final int MINIMUM_CAPACITY = 3;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * 返回最小容纳initialCapacity大小的2的幂的HashMap
     * @param initialCapacity 初始值
     * @return java.util.HashMap<K, V>
     * @author zhicong.lin
     * @date 2022/2/22 17:16
     */
    public static <K, V> HashMap<K, V> newHashMap(int initialCapacity) {
        if (initialCapacity == 0) {
            return newHashMap();
        }
        return new HashMap<>(initialCapacity);
    }

    /**
     * 初始化HashMap返回默认大小的HashMap
     * @return java.util.HashMap<K, V> 默认大小16的HashMap
     * @author zhicong.lin
     * @date 2022/2/22 17:06
     */
    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<>(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * 当参数值小于3，则返回容量为4的HashMap;
     * 否则返回容量比实际大小还大的2的幂大小的HashMap;
     * 此方法会相对newHashMap，如果固定大小打到2的幂这个阈值的时候，内存容量会比newHashMap大一倍;
     * 非固定长度时，可用此方法，减少扩容次数;
     * @param expectedSize 计算的大小值
     * @return java.util.HashMap<K, V> 比(expectedSize/0.75+1)大的2的幂的HashMap
     * @author zhicong.lin
     * @date 2022/2/22 17:10
     */
    public static <K, V> HashMap<K, V> newHashMapWithExpectedSize(int expectedSize) {
        int initialCapacity = expectedSize + 1;
        if (expectedSize >= MINIMUM_CAPACITY) {
            if (expectedSize < MAXIMUM_CAPACITY) {
                return newHashMap((int) ((float) expectedSize / DEFAULT_LOAD_FACTOR + 1.0F));
            }
            initialCapacity = Integer.MAX_VALUE;
        }
        return newHashMap(initialCapacity);
    }

}
