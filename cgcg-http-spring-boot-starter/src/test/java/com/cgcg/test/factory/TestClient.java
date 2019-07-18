package com.cgcg.test.factory;

import com.cgcg.rest.annotation.MappingFilter;
import com.cgcg.rest.annotation.RestClient;
import com.cgcg.test.controller.RequestFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@RestClient(url = "https://beta.user.api.91dkgj.com/userCenter/user/label/", fallback = TestClientImpl.class)
@MappingFilter(name = RequestFilter.class)
public interface TestClient {

    @PostMapping(value = "hasLabel")
    Map<String, Object> hasLabelPost(@RequestBody Map<String, Object> param, @RequestParam("channel") String channel);
    @GetMapping(value = "hasLabel")
    Map<String, Object> hasLabel(@RequestBody Map<String, Object> param, @RequestParam("channel") String channel);
}
