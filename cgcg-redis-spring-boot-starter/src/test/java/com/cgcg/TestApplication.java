package com.cgcg;

import org.cgcg.redis.core.annotation.EnableCgCgRedis;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * .
 *
 * @author zhicong.lin
 * @date 2019/6/20
 */
@EnableAspectJAutoProxy
@EnableCgCgRedis
@SpringBootApplication
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

}
