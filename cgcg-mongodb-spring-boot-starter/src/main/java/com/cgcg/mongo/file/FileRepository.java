package com.cgcg.mongo.file;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author zhicong.lin
 */
public interface FileRepository extends MongoRepository<FileGridFsInfo, String> {
}