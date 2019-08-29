package com.cgcg.test.controller;

import com.cgcg.test.factory.Act;
import com.cgcg.test.factory.TestClient;
import com.cgcg.test.factory.TestClient2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
@RestController
public class HttpController {
    @Resource
    private TestClient testClient;
    @Resource
    private TestClient2 testClient2;
    @GetMapping("/hasLabel")
    public Object result(String id, HttpServletRequest request) {
        HashMap<String, Object> param = new HashMap<>();
        param.put("accountId", id);
        param.put("accountId2", id);
        final Map<String, Object> h5 = testClient.hasLabelPost(param, "h5");
//        final long upload = testClient2.upload(file);
//        System.out.println("upload = " + upload);
        final Map<String, Object> objectMap = testClient2.hasLabel(param);
        return objectMap;
//        return h5;
    }

    @GetMapping("/load")
    public Object result(String id) {
        return testClient2.load("http://ss.bscstorage.com/miguan/dev-img-hhcr/data/image/faceID/201905/11133330190_20190507143742407.jpg", "123.jpg");
    }

    @PostMapping("/hehe")
    public Object hehe(@ModelAttribute Act act) {
        return act;
    }
    @PostMapping("/up")
    public Object up(@RequestPart MultipartFile newfile) {
        return newfile != null ? newfile.getSize() : 100000L;
    }
}
