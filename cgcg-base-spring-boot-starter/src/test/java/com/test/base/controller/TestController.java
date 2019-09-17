package com.test.base.controller;

import com.cgcg.base.core.exception.CommonException;
import com.cgcg.base.format.encrypt.Encrypt;
import com.cgcg.base.format.encrypt.EncryptController;
import com.cgcg.context.thread.ExecutorTask;
import com.cgcg.context.thread.ThreadPoolManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * .
 *
 * @author zhng.lin
 * @date 2019/6/24
 */@Slf4j
@Api(tags = "测试接口文档")
@EncryptController
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
    public Object get() throws Exception {
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("yes", "OK");
        final ExecutorTask task = new ExecutorTask() {
            @Override
            @SneakyThrows
            public void call() {
                for (int j = 0; j < 5; j++) {
                    Thread.sleep(300);
                    log.info(hashMap.toString());
                }
            }
        };
        ThreadPoolManager.execute(task);

        final Future<Map<String, Object>> submit = ThreadPoolManager.submit(() -> {
            final Map<String, Object> hashMap2 = new HashMap<>();
            hashMap2.put("NoNo", "No");
            return hashMap2;
        });
        return submit.get();
    }

    @ApiOperation("接口")
    @PutMapping
    public Map<String, Object> put() {
        final HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("yes", "OK");
        return hashMap;
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
