package com.cgcg.test;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cgcg.service.TestService;
import com.cgcg.service.Third;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
        PageHelper.startPage(1, 10);
        final LambdaQueryWrapper<Third> query = Wrappers.lambdaQuery(Third.class);
        query.gt(Third::getId, 500);
        final List<Third> list = testService.list(query);
        return PageInfo.of(list);
    }

    @PostMapping
    public Object test2(String name) {
       return testService.save(name);
    }

}
