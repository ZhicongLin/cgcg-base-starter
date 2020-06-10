package com.cgcg.service;

import javax.annotation.Resource;

import org.cgcg.redis.core.annotation.RedisCache;
import org.springframework.stereotype.Service;

import com.cgcg.TestMapper;

/**
 * .
 *
 * @author zhicong.lin
 * @date 2019/6/20
 */
@Service
public class TestService {


    @Resource
    TestMapper testMapper;

    @RedisCache(cache = "TestMapper")
    public Object findAll() {
        return this.testMapper.findAll();
    }
}
