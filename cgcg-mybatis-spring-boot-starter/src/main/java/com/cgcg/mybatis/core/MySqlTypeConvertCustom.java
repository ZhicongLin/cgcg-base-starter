package com.cgcg.mybatis.core;

import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.ITypeConvert;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;

/**
 * 自定义类型转换
 *
 * @author zhicong.lin
 */
public class MySqlTypeConvertCustom extends MySqlTypeConvert implements ITypeConvert {

    private static final String TINYINT = "tinyint(1)";

    @Override
    public IColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {
        String t = fieldType.toLowerCase();
        if (t.contains(TINYINT)) {
            return DbColumnType.BASE_BOOLEAN;
        }
        return super.processTypeConvert(globalConfig, fieldType);
    }
}