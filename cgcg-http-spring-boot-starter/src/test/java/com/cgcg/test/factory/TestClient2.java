package com.cgcg.test.factory;

import java.util.Map;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.cgcg.rest.annotation.DynamicParam;
import com.cgcg.rest.annotation.LoadMapping;
import com.cgcg.rest.annotation.MappingFilter;
import com.cgcg.rest.annotation.RestClient;
import com.cgcg.rest.annotation.UpLoadMapping;
import com.cgcg.test.controller.RequestFilter;

@RestClient(url = "http://localhost:8888")
@MappingFilter(name = RequestFilter.class)
public interface TestClient2 {

    @PostMapping(value = "hehe")
    Map<String, Object> hasLabel(@ModelAttribute Map<String, Object> param);

    @UpLoadMapping("up")
    long upload(@RequestPart("newfile") MultipartFile file);
    @LoadMapping
    Object load(@DynamicParam(isUrl = true) String url, @RequestParam("fileName") String fileName);
}
