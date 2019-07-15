package com.cgcg.test.controller;

import com.cgcg.rest.filter.RestFilter;
import com.cgcg.rest.param.RestHandle;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component
public class RequestFilter implements RestFilter {

    @Override
    public void postServer(String url, HttpMethod httpMethod, RestHandle<String, Object> restHandle, HttpHeaders headers, Class<?> returnType) {
        String bodyString = restHandle.getBodyString();
        System.out.println("bodyString = " + bodyString);
    }

}
