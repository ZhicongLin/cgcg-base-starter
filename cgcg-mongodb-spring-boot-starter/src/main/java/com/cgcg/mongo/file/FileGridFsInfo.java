package com.cgcg.mongo.file;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @Author 三分恶
 * @Date 2020/1/11
 * @Description 文档类
 */
@Data
@Document
@NoArgsConstructor
public class FileGridFsInfo {

    /**
     * 主键
     */
    @Id
    private String id;
    /**
     * 文件名称
     */
    private String name;
    /**
     * 文件内容
     */
    private String contentType;
    private long size;
    private Date uploadDate;
    private String md5;
    /**
     * 文件内容
     */
    private Binary content;
    /**
     * 文件路径
     */
    private String path;

}