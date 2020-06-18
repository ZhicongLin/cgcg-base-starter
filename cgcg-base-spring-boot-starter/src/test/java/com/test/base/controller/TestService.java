package com.test.base.controller;

import org.springframework.stereotype.Service;

import com.cgcg.base.validate.annotation.MethodValidate;

/**
 * .
 *
 * @author zhicong.lin
 * @date 2019/7/8
 */
@Service
public class TestService {
    @MethodValidate(value = AuthServiceImpl.class, method = "test")
    public void test(String vld) {
    }

}
