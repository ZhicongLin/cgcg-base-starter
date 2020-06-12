package com.cgcg.rest;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.cgcg.rest.http.RestBuilder;
import com.cgcg.rest.proxy.Proceeding;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RestClientRunner implements ApplicationRunner {
    private static final Set<Class<?>> REST_CLIENT_SET = new HashSet<>();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.debug("=============> Starting Rest Http Builder <=============");
        if (REST_CLIENT_SET.isEmpty()) {
            return;
        }
        for (Class<?> interfaceClass : REST_CLIENT_SET) {
            final Method[] methods = interfaceClass.getMethods();
            for (Method method : methods) {
                RestBuilder.getInstance(method, Proceeding.createLogName(interfaceClass, method));
            }
        }
        log.debug("=============> Finished Rest Http Builder <=============");
    }

    public static void add(String className) {
        try {
            final Class<?> aClass = Class.forName(className);
            REST_CLIENT_SET.add(aClass);
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
        }
    }
}