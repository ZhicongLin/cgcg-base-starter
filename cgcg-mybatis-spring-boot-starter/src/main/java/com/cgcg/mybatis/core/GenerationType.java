package com.cgcg.mybatis.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 自动生成类型枚举
 *
 * @author zhicong.lin
 * @date 2022-02-16 14:02
 **/
@Getter
@AllArgsConstructor
public enum GenerationType {
    /*
     * 生成外部文件夹
     */
    OUT_DIR("/generation"),
    /*
     * 生成到内部代码中
     */
    APPEND("/src");
    private final String dir;

}
