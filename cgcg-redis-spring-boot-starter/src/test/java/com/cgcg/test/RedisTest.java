package com.cgcg.test;

import com.cgcg.service.TestService;
import com.cgcg.service.User;
import org.cgcg.redis.core.entity.RedisHelper;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * .
 *
 * @author zhicong.lin
 * @date 2019/6/20
 */
@RestController
public class RedisTest {
    @Resource
    private TestService testService;
    @Resource
    private RedisHelper redisHelper;
    @GetMapping("/")
    public Object test() {
       return "hello";
    }

    @GetMapping("/get")
    public User get() {
        String key = "keys";
        return testService.getSer(key);
    }
    @PostMapping("/get")
    public User pu() {
        String key = "test-key";
        return testService.update(new User("u", "keys"));
    }
    @DeleteMapping("/get")
    public int de() {
        String key = "test-key";
        return testService.delete(new User("u", "keys"));
    }
}
