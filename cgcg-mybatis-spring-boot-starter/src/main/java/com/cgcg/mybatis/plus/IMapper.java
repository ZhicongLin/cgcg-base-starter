package com.cgcg.mybatis.plus;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * mapper 基础类
 *
 * @author zhicong.lin
 * @date 2022-02-16 09:33
 **/
public interface IMapper<T> extends BaseMapper<T> {

    /**
     * 批量添加
     *
     * @param list 列表
     * @return int
     * @author zhicong.lin
     * @date 2022-02-15 17:13
     */
    int insertBatch(@Param("list") List<T> list);
}
