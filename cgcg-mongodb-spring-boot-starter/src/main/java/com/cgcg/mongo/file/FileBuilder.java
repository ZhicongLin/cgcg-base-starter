package com.cgcg.mongo.file;

import com.cgcg.context.SpringContextHolder;
import com.cgcg.context.util.Md5Utils;
import com.cgcg.mongo.MongoDbBuilder;
import com.cgcg.mongo.QueryBuilder;
import com.cgcg.mongo.UpdateBuilder;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.Binary;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

/**
 * @program: cgcg-base-starter
 * @description: 文件操作构建器
 * @author: zhicong.lin
 * @create: 2022-02-07 13:50
 **/
public class FileBuilder extends MongoDbBuilder {

    private InputStream input;

    private FileGridFsInfo fileGridFsInfo;

    private String fileName;

    /**
     * 创建构建器
     *
     * @return org.cgcg.mongo.QueryBuilder
     * @author : zhicong.lin
     * @date : 2022/2/7 10:41
     */
    public static FileBuilder builder() {
        return new FileBuilder();
    }

    /**
     * 设置保存的文件名信息
     *
     * @param fileName
     * @return org.cgcg.mongo.file.FileBuilder
     * @author : zhicong.lin
     * @date : 2022/2/7 16:58
     */
    public FileBuilder fileName(String fileName) {
        if (StringUtils.isNotBlank(this.fileName)) {
            this.fileName = fileName + (this.fileName.substring(this.fileName.indexOf(".")));
        } else {
            this.fileName = fileName;
        }
        return this;
    }

    /**
     * 设置保存的文件信息
     *
     * @param file
     * @return org.cgcg.mongo.file.FileBuilder
     * @author : zhicong.lin
     * @date : 2022/2/7 16:58
     */
    public FileBuilder file(File file) {
        this.fileGridFsInfo = new FileGridFsInfo();
        try {
            Path path = file.toPath();
            this.input = Files.newInputStream(path);
            String fileName = path.getFileName().toString();
            fileGridFsInfo.setPath(file.getPath());
            String contentType = fileName.substring(fileName.indexOf(".") + 1);
            fileGridFsInfo.setContentType(FileMime.get(contentType));
            fileGridFsInfo.setName(fileName);
            if (StringUtils.isBlank(fileName)) {
                this.fileName = fileName;
            } else {
                this.fileName += "." + contentType;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 设置保存的文件信息
     *
     * @param file
     * @return org.cgcg.mongo.file.FileBuilder
     * @author : zhicong.lin
     * @date : 2022/2/7 16:58
     */
    public FileBuilder file(MultipartFile file) {
        this.fileGridFsInfo = new FileGridFsInfo();
        try {
            this.input = file.getInputStream();
            String originalFilename = file.getOriginalFilename();
            if (StringUtils.isNotBlank(originalFilename)) {
                String contentType = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
                fileGridFsInfo.setContentType(FileMime.get(contentType));
                fileGridFsInfo.setName(originalFilename);
                if (StringUtils.isBlank(fileName)) {
                    this.fileName = originalFilename;
                } else {
                    this.fileName += "." + contentType;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 保存文件到mongodb，当文件中存在md5一直的文件时，不重复保存数据，只对元数据生成一个副本，并返回
     *
     * @return org.cgcg.mongo.file.FileGridFsInfo
     * @author : zhicong.lin
     * @date : 2022/2/7 16:58
     */
    public FileGridFsInfo upload() {
        if (fileGridFsInfo == null) {
            fileGridFsInfo = new FileGridFsInfo();
        }
        try {
            byte[] bytes = FileCopyUtils.copyToByteArray(input);
            final String md5 = Md5Utils.md5(new String(bytes));
            final FileGridFsInfo load = QueryBuilder.builder().eq("md5", md5).findOne(FileGridFsInfo.class);
            if (load == null) {
                final Binary content = new Binary(bytes);
                fileGridFsInfo.setContent(content);
                fileGridFsInfo.setMd5(md5);
                fileGridFsInfo.setSize(bytes.length);
                fileGridFsInfo.setUploadDate(new Date());
                // 将文件存储到mongodb中,mongodb 将会返回这个文件的具体信息
                FileGridFsInfo info = repository().save(fileGridFsInfo);
                return saveGroup(info);
            }
            return saveGroup(load);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected FileRepository repository() {
        return SpringContextHolder.getBean(FileRepository.class);
    }

    /**
     * 加载文件信息
     *
     * @param id
     * @return org.cgcg.mongo.file.FileGridFsInfo
     * @author : zhicong.lin
     * @date : 2022/2/7 16:57
     */
    public FileGridFsInfo load(String id) {
        final FileGridFsGroup group = QueryBuilder.builder().eq("id", id).findOne(FileGridFsGroup.class);
        if (group == null) {
            return repository().findById(id).orElse(null);
        }
        final String infoId = group.getInfoId();
        if (infoId == null) {
            return null;
        }
        final FileGridFsInfo info = repository().findById(infoId).orElse(null);
        if (info != null) {
            info.setName(group.getFileName());
            info.setId(group.getId());
        }
        return info;
    }

    /**
     * 刪除文件
     * 当文件多个副本时，删除单个副本，保留元数据
     * 当文件单个副本时，删除副本，并删除元数据
     *
     * @param id
     * @return void
     * @author : zhicong.lin
     * @date : 2022/2/7 16:56
     */
    public void delete(String id) {
        final FileGridFsGroup group = QueryBuilder.builder().eq("id", id).findOne(FileGridFsGroup.class);
        if (group == null) {
            repository().deleteById(id);
            return;
        }
        final String infoId = group.getInfoId();
        final long idCount = QueryBuilder.builder().eq("infoId", infoId).count(FileGridFsGroup.class);
        QueryBuilder.builder().eq("id", id).delete(FileGridFsGroup.class);
        if (idCount == 1) {
            repository().deleteById(infoId);
        }
    }

    private FileGridFsInfo saveGroup(FileGridFsInfo info) {
        FileGridFsGroup group = new FileGridFsGroup();
        group.setFileName(fileName);
        group.setInfoId(info.getId());
        group.setUploadDate(new Date());
        UpdateBuilder.builder(FileGridFsGroup.class).saveOrUpdate(group);
        String id = group.getId();
        info.setId(id);
        info.setName(fileName);
        return info;
    }
}
