package com.cgcg.mybatis.core;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.baomidou.mybatisplus.annotation.IdType;

/**
 * 代码自动生成配置
 *
 * @author zhicong.lin
 * @date 2022-02-16 14:00
 **/
@Setter
@Getter
@Accessors(chain = true)
public class MybatisGenerationProperties {
    private String author = "zhicong.lin";
    private String[] tablePrefix;
    private IdType idType = IdType.AUTO;
    private GenerationType generationType = GenerationType.OUT_DIR;
    private String logicDelete = "del";

    private String dataSourceUrl;
    private String dataSourceDriver;
    private String dataSourceUser;
    private String dataSourcePwd;
}
