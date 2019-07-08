package com.test.base.controller;

import com.cgcg.base.validate.annotation.Validate;
import org.springframework.stereotype.Service;

/**
 * .
 *
 * @author zhicong.lin
 * @date 2019/7/8
 */
@Service
public class TestService {

    @Validate(value = AuthServiceImpl.class, method = "test")
    public void test(String vld) {

    }
}
