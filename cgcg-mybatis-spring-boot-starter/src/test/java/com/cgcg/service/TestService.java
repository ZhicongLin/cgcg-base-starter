package com.cgcg.service;

import javax.annotation.Resource;

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

    public Object findAll() {
        return this.testMapper.findAll();
    }
}
