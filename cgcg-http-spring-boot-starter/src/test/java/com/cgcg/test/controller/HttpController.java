package com.cgcg.test.controller;

import com.cgcg.test.factory.TestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;

@RestController
public class HttpController {
    @Resource
    private TestClient testClient;
    @GetMapping("/hasLabel")
    public Object result() {
        HashMap<String, Object> param = new HashMap<>();
        param.put("accountId", "8c503264549f4866a686ad67c7f87c2d");
        return testClient.hasLabel(param, "h5");
    }
}
