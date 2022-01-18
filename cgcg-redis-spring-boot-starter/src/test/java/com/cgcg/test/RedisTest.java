package com.cgcg.test;

import com.alibaba.fastjson.JSON;
import com.cgcg.service.RedisMQtest;
import com.cgcg.service.TestService;
import com.cgcg.service.User;
import org.cgcg.redis.core.entity.RedisHelper;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

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
    @Resource
    private RedisMQtest redisMQtest;
    @GetMapping("/")
    public Object test() {
       return "hello";
    }

    @GetMapping("/get")
    public Object get(String key) {
//        String key = "keys";
//        for (int i = 0; i < 10; i++) {
//            testService.getSer(key + i);
//        }
//        Object o = this.redisHelper.get("TestService:keys4");
//        String s = JSON.toJSONString(o);
//        System.out.println("s = " + s);
//        User ser = testService.getSer(key);
//        String s1 = JSON.toJSONString(ser);
        return  testService.get(key);
    }

    @PostMapping("/get")
    public Object pu(String key) {
//        String key = "test-key";
//        final User update = testService.update(new User("u", "keys"));
        final Map<String, Object> cache = testService.cache(key);
//        redisMQtest.producer(cache);
        return cache;
    }
    @DeleteMapping("/get")
    public int de() {
        String key = "test-key";
        return testService.delete(new User("u", "keys"));
    }
}
