package com.cgcg.service;

import org.cgcg.redis.core.annotation.RedisCache;
import org.cgcg.redis.core.annotation.RedisNameSpace;
import org.cgcg.redis.core.annotation.Rmqp;
import org.cgcg.redis.core.enums.RedisEnum;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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

    @RedisCache(key = "#user.code", expire = {"30", "50"}, type = RedisEnum.FLUSH)
    public User update(User user) {
        return new User(user.getName() + "asd", "sdf");
    }

    @Rmqp(value = {"Redis::MQ1", "Redis::MQ2"})
    @RedisCache(key = "#key", expire = {"30", "500"}, type = RedisEnum.UPD)
    public Map<String, Object> cache(String key) {
        final Map<String, Object> map = new HashMap<>();
        map.put(key, "haha" + key);
        return map;
    }

    @Rmqp(value = {"Redis::MQ1"}, retry = 3)
    @RedisCache(key = "#key", expire = {"30", "500"})
    public Map<String, Object> get(String key) {
        if (key.equals("key2")) {
            return null;
        }
        final Map<String, Object> map = new HashMap<>();
        map.put(key, "haha" + key);
        map.put(key + "1", "haha" + key);
        map.put(key + "2", "haha" + key);
        map.put(key + "3", "haha" + key);
        return map;
    }

    @RedisCache(key = "#user.code", type = RedisEnum.DEL)
    public int delete(User user) {
        return 1;
    }

}
