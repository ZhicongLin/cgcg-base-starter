package com.cgcg.jobs.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.cgcg")
@SpringBootApplication
class CgcgJobsClientStarterApplicationTests {

    public static void main(String[] args) {
        SpringApplication.run(CgcgJobsClientStarterApplicationTests.class, args);
    }
}
