package com.cgcg;

import org.cgcg.redis.mybatis.annotation.EnableMyBatisCache;
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
@EnableMyBatisCache
@SpringBootApplication
public class MybatisTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybatisTestApplication.class, args);
    }

}
