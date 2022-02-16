package com.cgcg.mybatis.plus;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 基础服务实现类
 *
 * @author zhicong.lin
 * @date 2022-02-15 15:54
 **/
@Slf4j
public class BaseServiceImpl<M extends IMapper<T>, T> extends ServiceImpl<M, T> {

    /**
     * 批量添加系统用户
     *
     * @param list 列表数据
     * @return int
     * @author zhicong.lin
     * @date 2022-02-15 15:44
     */
    @Transactional(rollbackFor = Exception.class)
    public int addList(List<T> list) {
        baseMapper.insertBatch(list);
        return 1;
    }

}
