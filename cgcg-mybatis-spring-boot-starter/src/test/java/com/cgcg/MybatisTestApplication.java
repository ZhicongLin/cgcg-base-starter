package com.cgcg;

import com.cgcg.base.language.EnableLanguage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * .
 *
 * @author zhicong.lin
 * @date 2019/6/20
 */
@EnableLanguage
@SpringBootApplication
public class MybatisTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybatisTestApplication.class, args);
    }

}
