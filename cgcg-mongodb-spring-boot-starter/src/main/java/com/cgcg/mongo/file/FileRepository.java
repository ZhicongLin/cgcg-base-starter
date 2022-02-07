package com.cgcg.mongo.file;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author zhicong.lin
 */
@Repository
public interface FileRepository extends MongoRepository<FileGridFsInfo, String> {
}