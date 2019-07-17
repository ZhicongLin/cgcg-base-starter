package com.cgcg;

import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.cgcg.redis.mybatis.RedisCacheManager;

import java.util.List;
import java.util.Map;

/**
 * .
 *
 * @author zhicong.lin
 * @date 2019/6/24
 */
@Mapper
@CacheNamespace(implementation = RedisCacheManager.class)
public interface TestMapper {

    @Select("select * from cl_user")
    List<Map<String ,Object>> findAll();
    @Update("update cl_user1 set login_name = '2222' where login_name = '1212'")
    int update();
}
