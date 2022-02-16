package com.cgcg.mongo;

import com.cgcg.mongo.core.QueryBuilder;
import com.cgcg.mongo.core.UpdateBuilder;
import com.cgcg.mongo.document.MongoTestObj;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @program: cgcg-base-starter
 * @description: 文件
 * @author: zhicong.lin
 * @create: 2022-02-07 14:31
 **/
@RestController
@RequestMapping("/test")
public class MongoController {

    @PostMapping
    public void save() {
        for (int i = 0; i < 20; i++) {
            MongoTestObj mongoTestObj = new MongoTestObj();
            mongoTestObj.setName("name" + RandomUtils.nextInt());
            mongoTestObj.setCreateTime(new Date());
            UpdateBuilder.builder(MongoTestObj.class).saveOrUpdate(mongoTestObj);
        }
    }

    /**
     * 在线显示文件
     */
    @GetMapping("/page")
    @ResponseBody
    public Page<MongoTestObj> page(Integer pageNum, Integer pageSize) {
        return QueryBuilder.builder().pageable(pageNum, pageSize)
                .desc("name")
                .findPage(MongoTestObj.class);
    }
}
