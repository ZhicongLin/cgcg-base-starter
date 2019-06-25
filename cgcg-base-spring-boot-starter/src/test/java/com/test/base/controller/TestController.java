package com.test.base.controller;

import com.cgcg.base.exception.CommonException;
import com.cgcg.base.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * .
 *
 * @author zhng.lin
 * @date 2019/6/24
 */
@Api(tags = "测试接口文档")
@Controller
@RequestMapping("test")
public class TestController {

    @ApiOperation("接口")
    @GetMapping("index")
    public String index() {
        final HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("yes", "OK");
        return "index";
    }
    @ApiOperation("接口")
    @GetMapping
    @ResponseBody
    public Result<Map<String, Object>> get() {
        final HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("yes", "OK");
        return Result.success(hashMap);
    }

    @ApiOperation("接口")
    @PutMapping
    @ResponseBody
    public Map<String, Object> put() {
        final HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("yes", "OK");
        return hashMap;
    }

    @ApiOperation("POST接口")
    @PostMapping
    @ResponseBody
    public List gets(Integer id) {
        if (id == 1) {
            throw new CommonException(123, "zsd");
        }
        final HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("yes", "OK");

        final ArrayList arrayList = new ArrayList();
        arrayList.add(hashMap);
        return arrayList;
    }
}
