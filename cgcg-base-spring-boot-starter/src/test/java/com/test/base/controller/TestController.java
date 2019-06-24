package com.test.base.controller;

import com.cgcg.base.vo.ResultMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * .
 *
 * @author zhng.lin
 * @date 2019/6/24
 */
@Api(tags = "测试接口文档")
@RestController
@RequestMapping("test")
public class TestController {

    @ApiOperation("接口")
    @GetMapping
    public ResultMap<Map<String, Object>> get() {
        final HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("yes", "OK");
        return ResultMap.success(hashMap);
    }
}
