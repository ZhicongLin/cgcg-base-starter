package com.cgcg;

import org.cgcg.redis.core.annotation.EnableRedisMQ;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * .
 *
 * @author zhicong.lin
 * @date 2019/6/20
 */
@EnableRedisMQ
@EnableAspectJAutoProxy
@SpringBootApplication
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

}
