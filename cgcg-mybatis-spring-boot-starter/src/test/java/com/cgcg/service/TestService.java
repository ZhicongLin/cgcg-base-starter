package com.cgcg.service;

import com.cgcg.TestMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
