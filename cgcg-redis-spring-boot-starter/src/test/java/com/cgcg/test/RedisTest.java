package com.cgcg.test;

import java.util.concurrent.ExecutorService;

import javax.annotation.Resource;

import org.cgcg.redis.core.RedisHelper;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @Resource
    private ExecutorService executorService;
    @GetMapping("/")
    public Object test(String key) {
       return "hello";
    }

    @GetMapping("/get")
    public Object get(String key) {
        key = "keys";
        for (int i = 0; i < 10; i++) {
            testService.getSer(key + i);
        }

       /*for (int i = 0; i < 30; i++) {
            final int index = i;
            executorService.execute(()->{
                final User user = testService.lockTest(key);
                System.out.println(index + "user = " + user);
            });
        }*/
        return  testService.getSer(key);
    }
    @PostMapping("/get")
    public User pu(String key) {
        key = "test-key";
        return testService.update(new User("u", "keys"));
    }
    @DeleteMapping("/get")
    public int de(String key) {
         key = "test-key";
        return testService.delete(new User("u", "keys"));
    }
}
