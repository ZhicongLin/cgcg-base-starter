package com.cgcg.test.factory;

import com.cgcg.rest.annotation.*;
import com.cgcg.test.controller.RequestFilter;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestClient(url = "http://localhost:6666")
@MappingFilter(name = RequestFilter.class)
public interface TestClient2 {

    @PostMapping(value = "hehe")
    Map<String, Object> hasLabel(@ModelAttribute Map<String, Object> param);
    @UpLoadMapping("up")
    long upload(@RequestPart("newfile") MultipartFile file);
    @LoadMapping
    Object load(@DynamicParam(isUrl = true) String url, @RequestParam("fileName") String fileName);
}
