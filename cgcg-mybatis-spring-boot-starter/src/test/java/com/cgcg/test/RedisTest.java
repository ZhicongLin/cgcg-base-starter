package com.cgcg.test;

import com.cgcg.service.TestService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * .
 *
 * @author zhicong.lin
 * @date 2019/6/20
 */
@Api
@RestController
public class RedisTest {
    @Resource
    private TestService testService;

    @GetMapping("/")
    public Object test(HttpServletRequest request) {
        String token = request.getHeader("token");
        System.out.println("token = " + token);
        return testService.findAll();
    }

    @PostMapping
    public Object test2(String name) {
       return testService.save(name);
    }

}
