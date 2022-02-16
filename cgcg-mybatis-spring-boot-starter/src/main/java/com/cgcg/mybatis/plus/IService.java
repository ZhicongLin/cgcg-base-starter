package com.cgcg.mybatis.plus;

import java.util.List;

/**
 * 扩展IService
 *
 * @author zhicong.lin
 * @date 2022-02-16 09:19
 **/
public interface IService<T> extends com.baomidou.mybatisplus.extension.service.IService<T> {
    /**
     * 批量添加系统菜单信息
     *
     * @param list 列表数据
     * @return int
     * @author zhicong.lin
     * @date 2022-02-15 17:19
     */
    int addList(List<T> list);
}

