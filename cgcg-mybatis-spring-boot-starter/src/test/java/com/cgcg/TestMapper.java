package com.cgcg;

import org.apache.ibatis.annotations.*;
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
    @Update("update sys_user set user_name = '2222' where id = '1'")
    int update();

    @Insert("insert into sys_user (user_name) values (1)")
    int insert();
}
