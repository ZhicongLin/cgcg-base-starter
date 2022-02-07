package com.cgcg.mongo.file;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @program: cgcg-base-starter
 * @description: 文件映射关系
 * @author: zhicong.lin
 * @create: 2022-02-07 16:08
 **/
@Data
@Document
public class FileGridFsGroup {
    @Id
    private String id;
    private String fileName;
    private String infoId;
    private Date uploadDate;
}
