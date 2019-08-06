package com.test.base.controller;

import com.cgcg.base.core.exception.CommonException;
import com.cgcg.base.format.Result;
import com.cgcg.base.format.encrypt.Encrypt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
@RestController
@RequestMapping("test")
public class TestController {
    @Resource
    private TestService testService;
    @ApiOperation("接口")
    @GetMapping("index")
    public String index() {
        String vld = "1";
        this.testService.test(vld);
        final HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("yes", "OK");
        return "index";
    }
    @ApiOperation("接口")
    @GetMapping
    public Result get() {
        final HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("yes", "OK");
        return Result.success(hashMap);
    }

    @ApiOperation("接口")
    @PutMapping
    public Map<String, Object> put() {
        final HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("yes", "OK");
        return null;
    }

    @ApiOperation("POST接口")
    @Encrypt(enable = false)
    @PostMapping
    public List gets(@RequestBody Map<String, String> param) {
        if (Integer.valueOf(param.get("id")) == 1) {
            throw new CommonException(123, "zsd");
        }
        final HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("yes", "OK");
        hashMap.put("id", "11");

        final ArrayList arrayList = new ArrayList();
        arrayList.add(hashMap);
        return arrayList;
    }
}
