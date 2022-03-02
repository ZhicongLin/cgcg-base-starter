package com.cgcg;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cgcg.service.Third;
import org.apache.ibatis.annotations.Mapper;

/**
 * .
 * @author zhicong.lin
 * @date 2019/6/24
 */
@Mapper
//@CacheNamespace(implementation = RedisCacheManager.class)
public interface TestMapper extends BaseMapper<Third> {

}
