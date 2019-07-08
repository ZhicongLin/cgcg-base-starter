package com.cgcg.service;

import com.cgcg.base.validate.annotation.Validate;
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

    @Validate(value = TestValidate.class, method = "vali")
    @RedisCache(key = "#p0", type = RedisEnum.SEL)
    public User getSer(String key) {
        return new User("ahaha", key);
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
