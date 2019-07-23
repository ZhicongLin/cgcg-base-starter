package com.cgcg.test.factory;

import java.util.HashMap;
import java.util.Map;

public class TestClientImpl implements TestClient {

    @Override
    public Map<String, Object> hasLabelPost(Map<String, Object> param, String channel) {
        return null;
    }

    @Override
    public Map<String, Object> path(Map<String, Object> param, String channel, String hl) {
        return null;
    }

    @Override
    public Map<String, Object> hasLabel(Map<String, Object> param, String channel) {
        System.out.println("param = 又进来");
        final HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("error", "尼玛有问题");
        return hashMap;
    }
}
