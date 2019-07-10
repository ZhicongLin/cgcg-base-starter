package com.cgcg.service;

import org.cgcg.redis.core.annotation.RedisCache;
import org.cgcg.redis.core.annotation.RedisNameSpace;
import org.cgcg.redis.core.enums.RedisEnum;
import org.springframework.stereotype.Service;

/**
 * .
 *
 * @author zhicong.lin
 * @date 2019/6/20
 */
@Service
@RedisNameSpace(cache = "TestService", expire = "test.time")
public class TestService {

    @RedisCache(key = "#p0", type = RedisEnum.SEL)
    public User getSer(String key) {
        return new User("哈哈", key);
    }

    @RedisCache(key = "#user.code", expire = "30", type = RedisEnum.FLUSH)
    public User update(User user) {
        return new User(user.getName() + "asd", "sdf");
    }

    @RedisCache(key = "#user.code", type = RedisEnum.DEL)
    public int delete(User user) {
        return 1;
    }

}
