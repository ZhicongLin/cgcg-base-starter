package com.cgcg.service;

import org.cgcg.redis.core.annotation.RedisCache;
import org.cgcg.redis.core.annotation.RedisLock;
import org.cgcg.redis.core.annotation.RedisNameSpace;
import org.cgcg.redis.core.enums.RedisExecuteType;
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

    @RedisCache(key = "#p0", type = RedisExecuteType.SELECT)
    public User getSer(String key) {
        return new User("哈哈", key);
    }

    @RedisCache(key = "#user.code", expire = "30", type = RedisExecuteType.FLUSH, lock = true)
    public User update(User user) {
        return new User(user.getName() + "asd", "sdf");
    }

    @RedisCache(key = "#user.code", type = RedisExecuteType.DELETE)
    public int delete(User user) {
        return 1;
    }

    @RedisLock(key = "#p0", unlock = false)
    @RedisCache(key = "#p0", type = RedisExecuteType.SELECT)
    public User lockTest(String key) {
        return new User("haLock", key);
    }
}
