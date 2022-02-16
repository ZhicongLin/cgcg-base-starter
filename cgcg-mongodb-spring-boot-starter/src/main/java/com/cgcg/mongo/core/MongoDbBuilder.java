package com.cgcg.mongo.core;

import com.cgcg.context.SpringContextHolder;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @description: mongodb 构建器
 * @author: zhicong.lin
 * @create: 2022-02-07 11:24
 **/
public class MongoDbBuilder {

    protected static final String ID = "id";

    /**
     * 获取mongoTemplate
     *
     * @return org.springframework.data.mongodb.core.MongoTemplate
     * @author : zhicong.lin
     * @date : 2022/2/7 11:55
     */
    protected MongoTemplate getTemplate() {
        return MongoDbBuilderHolder.MONGO_TEMPLATE;
    }

    static class MongoDbBuilderHolder {
        private static final MongoTemplate MONGO_TEMPLATE = SpringContextHolder.getBean(MongoTemplate.class);
    }
}
