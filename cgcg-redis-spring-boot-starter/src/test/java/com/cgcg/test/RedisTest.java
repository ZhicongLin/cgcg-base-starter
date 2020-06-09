package com.cgcg.test;

import javax.annotation.Resource;

import org.cgcg.redis.core.RedisHelper;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.cgcg.service.TestService;
import com.cgcg.service.User;

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
    public Object get() {
        String key = "keys";
        for (int i = 0; i < 10; i++) {
            testService.getSer(key + i);
        }
        Object o = this.redisHelper.get("TestService:keys4");
        String s = JSON.toJSONString(o);
        System.out.println("s = " + s);
        User ser = testService.getSer(key);
        String s1 = JSON.toJSONString(ser);
        return  s1;
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
