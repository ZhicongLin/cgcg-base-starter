package com.test.base;

import com.cgcg.base.encrypt.EnableEncryptApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * .
 *
 * @author zhicong.lin
 * @date 2019/6/24
 */
@EnableEncryptApi
@SpringBootApplication
public class BaseTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaseTestApplication.class, args);
    }
}
