package com.cgcg.mongo;

import com.cgcg.mongo.file.FileBuilder;
import com.cgcg.mongo.file.FileGridFsInfo;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @program: cgcg-base-starter
 * @description: 文件
 * @author: zhicong.lin
 * @create: 2022-02-07 14:31
 **/
@RestController
@RequestMapping("/files")
public class FileController {

    @PostMapping
    public FileGridFsInfo upload() {
        File file = new File("D:\\zhile_agent_po.zip");
        FileGridFsInfo upload = FileBuilder.builder().file(file).upload();
        System.out.println("upload = " + upload);
        return upload;
    }

    /**
     * 上传接口
     */
    @PostMapping("/upload")
    @ResponseBody
    public FileGridFsInfo handleFileUpload(@RequestParam("file") MultipartFile file, String fileName) {
        FileGridFsInfo upload = FileBuilder.builder()
                .file(file)
                .fileName(fileName)
                .upload();
        System.out.println("upload = " + upload);
        return upload;
    }


    /**
     * 上传接口
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public String delete(@PathVariable String id) {
        FileBuilder.builder().delete(id);
        return id;
    }

    /**
     * 在线显示文件
     */
    @GetMapping("/view")
    @ResponseBody
    public ResponseEntity<Object> serveFileOnline(@RequestParam("id") String id) throws UnsupportedEncodingException {
        FileGridFsInfo file = FileBuilder.builder().load(id);
        if (file != null) {
            String name =  URLEncoder.encode(file.getName(), "UTF-8");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "fileName=" + name)
                    .header(HttpHeaders.CONTENT_TYPE, file.getContentType())
                    .header("Connection", "close")
                    .header(HttpHeaders.CONTENT_LENGTH , file.getSize() + "")
                    .body(file.getContent().getData());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File was not found");
        }
    }

    @GetMapping
    public ResponseEntity<Object> load(String id) throws UnsupportedEncodingException {
        FileGridFsInfo fileGridFsInfo = FileBuilder.builder().load(id);
        if (fileGridFsInfo != null) {
            String name =  URLEncoder.encode(fileGridFsInfo.getName(), "UTF-8");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=" + name)
                    .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                    .header(HttpHeaders.CONTENT_LENGTH, fileGridFsInfo.getSize() + "")
                    .header("Connection", "close")
                    .body(fileGridFsInfo.getContent().getData());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File was not fount");
        }
    }
}
