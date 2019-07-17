package com.cgcg.test;

import com.cgcg.rest.annotation.EnableRestClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRestClients
@SpringBootApplication
public class HttpApp {

    public static void main(String[] args) {
        SpringApplication.run(HttpApp.class, args);
    }
}
