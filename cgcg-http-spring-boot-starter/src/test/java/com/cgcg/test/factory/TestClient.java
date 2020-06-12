package com.cgcg.test.factory;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.cgcg.rest.annotation.MappingFilter;
import com.cgcg.rest.annotation.RestClient;
import com.cgcg.test.controller.RequestFilter;

@RestClient(url = "https://beta.user.api.91dkgj.com/userCenter/user/label/", fallback = TestClientImpl.class)
@MappingFilter(name = RequestFilter.class)
public interface TestClient {

    @PostMapping(value = "hasLabel")
    Map<String, Object> hasLabelPost(@RequestBody Map<String, Object> param, @RequestParam("channel") String channel);

    @PostMapping(value = "/{hl}")
    Map<String, Object> path(@RequestBody Map<String, Object> param, @RequestParam("channel") String channel, @PathVariable("hl") String hl);

    @GetMapping(value = "hasLabel")
    Map<String, Object> hasLabel(@RequestBody Map<String, Object> param, @RequestParam("channel") String channel);
}
