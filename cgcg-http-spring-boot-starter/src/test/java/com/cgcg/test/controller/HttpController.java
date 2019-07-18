package com.cgcg.test.controller;

import com.cgcg.test.factory.TestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HttpController {
    @Resource
    private TestClient testClient;
    @GetMapping("/hasLabel")
    public Object result(String id) {
        HashMap<String, Object> param = new HashMap<>();
        param.put("accountId", id);
        final Map<String, Object> h5 = testClient.hasLabelPost(param, "h5");
        testClient.hasLabel(param, "h5");
        return h5;
    }
}
