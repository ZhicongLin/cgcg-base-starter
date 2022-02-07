package com.cgcg.mongo.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @program: cgcg-base-starter
 * @description: test mongo entity
 * @author: zhicong.lin
 * @create: 2022-02-07 09:59
 **/
@Data
@Document("test")
public class MongoTestObj implements Serializable {
    @Id
    private String id;
    private String name;
}
