package com.cgcg.rest.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cgcg.rest.exception.ErrorFactory;
import com.cgcg.rest.exception.NullFileException;
import com.cgcg.rest.exception.RestException;
import com.cgcg.rest.param.RestHandle;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import sun.security.action.GetPropertyAction;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.AccessController;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * RestTemplate
 */
@Slf4j
@Component
public class RestTemplateFactory {

    @Resource
    private RestTemplate restTemplate;
    @Resource
    private HttpServletResponse response;

    // 解决下载时中文乱码问题!!!!
    private static String encodingFileName(String fileName) {
        String returnFileName = "";
        try {
            returnFileName = URLEncoder.encode(fileName, "UTF-8");
            returnFileName = org.springframework.util.StringUtils.replace(returnFileName, "+", "%20");
            if (returnFileName.length() > 150) {
                returnFileName = new String(fileName.getBytes("GB2312"), "ISO8859-1");
                returnFileName = org.springframework.util.StringUtils.replace(returnFileName, " ", "%20");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return returnFileName;
    }

    /**
     * @throws Exception the exception
     * @Description: 文件下载
     * @author nengneng.lian
     * @date 2017/10/26 20:44
     */
    private void loadFileOutputStream(ResponseEntity<byte[]> result, String fileName) throws RestException {
        byte[] bytes = result.getBody();
        if (bytes == null) {
            return;
        }
        if (response.getHeader("content-disposition") == null) {
            //3.设置content-disposition响应头控制浏览器以下载的形式打开文件
            response.setHeader("content-disposition", "attachment; filename=" + encodingFileName(fileName));
        }
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            out.write(bytes);
            out.flush();
        } catch (IOException e) {
            throw new RestException(500, "文件下载IO异常");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

    }

    /**
     * 文件下载
     *
     * @param url 文件地址，可带参数，如：url="http://1.1.2.10/oss/aaa/bbbb/ccc/TestFileUpload.jpg?companyId=1&ossType=material"
     * @return 二进制流
     */
    public byte[] loadFileByte(String url, RestHandle<String, Object> params, HttpHeaders headers, HttpMethod httpMethod) {
        final StringBuilder path = new StringBuilder(url);
        if (params != null && params.size() > 0) {
            if (url.indexOf("?") <= 0) {
                path.append("?");
            } else if (!url.endsWith("?") && !url.endsWith("&")) {
                path.append("&");
            }

            for (String key : params.keySet()) {
                path.append(key).append("=").append(params.get(key)).append("&");
            }
        }
        final HttpEntity<byte[]> requestEntity = new HttpEntity<>(headers);
        try {
            final HttpMethod method = httpMethod != null ? httpMethod : HttpMethod.GET;
            ResponseEntity<byte[]> result = restTemplate.exchange(path.toString(), method, requestEntity, byte[].class);
            if (result.getStatusCode().is2xxSuccessful()) {

                HttpHeaders resultHeaders = result.getHeaders();
                for (String key : resultHeaders.keySet()) {
                    List<String> headList = resultHeaders.get(key);
                    if (headList == null) {
                        continue;
                    }
                    for (String hv : headList) {
                        this.response.setHeader(key, hv);
                    }
                }
                if (params != null && params.getDown() != null && params.getDown()) {
                    loadFileOutputStream(result, String.valueOf(params.get("fileName")));
                    return null;
                } else {
                    return result.getBody();
                }
            } else {
                throw new RestException(500, "下载异常");
            }
        } catch (HttpClientErrorException hse) {
            log.error(hse.getMessage(), hse);
            throw new RestException(hse.getStatusCode().value(), hse.getStatusCode().getReasonPhrase());
        } catch (HttpServerErrorException hse) {
            log.error(hse.getMessage(), hse);
            throw httpErrorMsg(hse);
        }

    }

    public <T> T execute(String url, HttpMethod httpMethod, RestHandle<String, Object> params, HttpHeaders httpHeaders, Class<T> resultType) {
        HttpEntity httpEntity;
        if (httpMethod.equals(HttpMethod.GET) || httpMethod.equals(HttpMethod.DELETE) || httpMethod.equals(HttpMethod.HEAD)) {
            params.getUriParams().putAll(params);
            httpEntity = new HttpEntity<>(httpHeaders);
        } else {
            httpEntity = new HttpEntity<>(params.getBodyString() != null ? params.getBodyString() : params, httpHeaders);
        }
        final StringBuilder pathBuilder = new StringBuilder(url);
        this.fatchUri(params.getUriParams(), pathBuilder);
        return this.executeHttpRequest(pathBuilder.toString(), httpMethod, httpEntity, resultType, params);
    }

    private void fatchUri(Map<String, Object> params, StringBuilder pathBuilder) {
        if (params == null || params.size() <= 0) {
            return;
        }
        for (String key : params.keySet()) {
            if (pathBuilder.indexOf("?") <= 0) {
                pathBuilder.append("?");
            } else {
                pathBuilder.append("&");
            }
            pathBuilder.append(key).append("=").append("{").append(key).append("}");
        }
    }

    /**
     * @return RestException
     * @Description: 捕获微服务接口中返回的异常信息, 封装后再抛出
     * @author nengneng.lian
     * @date 2017/11/15 10:50
     */
    private RestException httpErrorMsg(HttpServerErrorException e) {
        log.error("服务异常原始信息：" + e.getResponseBodyAsString());
        // 异常码
        String errorCode = e.getStatusCode().toString();
        // 异常消息
        String errorMsg = e.getMessage();
        // 获取异常body
        if (StringUtils.isNotBlank(e.getResponseBodyAsString())) {
            JSONObject jsonError = JSON.parseObject(e.getResponseBodyAsString());
            // errorCode存在且为数字
            if (jsonError.get("errorCode") != null && StringUtils.isNumeric(jsonError.get("errorCode").toString())) {
                errorCode = jsonError.get("errorCode").toString();
            }
            // errorMsg存在
            if (jsonError.get("errorMsg") != null) { // 微服务封装errorMsg的情况下选择errorMsg
                errorMsg = jsonError.get("errorMsg").toString();
            } else if (jsonError.get("errorMsg") != null) { // 微服务没有封装则调用SpringBoot自带异常消息
                errorMsg = jsonError.get("errorMsg").toString();
            }
        }
        // 抛出异常
        return new RestException(Integer.parseInt(errorCode), errorMsg);
    }

    /**
     * 调用http请求的执行方法，如果返回值里有body则返回body，没有则返回空，异常则抛异常
     *
     * @param url          请求的url地址
     * @param httpMethod   请求类型，GET  POST  PUT  DELETE  等
     * @param httpEntity   请求参数
     * @param responseType 返回值类型
     * @param <T>
     * @return
     */
    private <T> T executeHttpRequest(String url, HttpMethod httpMethod, HttpEntity<?> httpEntity, Class<T> responseType, Map<String, Object> params) {
        try {
            final HttpEntity<T> response = this.restTemplate.exchange(url, httpMethod, httpEntity, responseType, params);
            if (response.hasBody()) {
                return response.getBody();
            }
            return null;
        } catch (HttpClientErrorException e) {
            String responseBodyAsString = e.getResponseBodyAsString();
            ErrorFactory error = ErrorFactory.getError(responseBodyAsString);
            if (error != null) {
                throw new RestException(error.getErrorCode(), error.getErrorMsg());
            } else {
                final HttpStatus statusCode = e.getStatusCode();
                throw new RestException(statusCode.value(), String.format("链接请求失败[%s]", statusCode.getReasonPhrase()));
            }
        } catch (HttpServerErrorException e) {
            throw httpErrorMsg(e); // 异常处理
        } catch (ResourceAccessException e) {
            log.error(e.getMessage(), e);
            if (Objects.requireNonNull(e.getMessage()).contains("java.net.ConnectException")) {
                throw new RestException(404, "服务连接失败");
            }
            throw new RestException(403, "资源不可用");
        }
    }

    /**
     * @return http响应 response
     * @Description: 文件上传
     * @author nengneng.lian
     * @date 2017/10/31 14:21
     */
    public <T> T uploadFile(String url, RestHandle<String, Object> params, HttpHeaders httpHeaders, Class<T> responseType) throws RestException {
        File tempFile = null;
        // 发送POST请求
        try {
            tempFile = saveTempFile(params);
            if (tempFile == null) {
                throw new NullFileException();
            }
            // 请求参数
            MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
            //组装普通的post格式数据
            if (params.size() > 0) {
                for (String str : params.keySet()) {
                    formData.add(str, params.get(str));
                }
            }
            // 设置请求体
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(formData, httpHeaders);
            T result = restTemplate.postForObject(url, requestEntity, responseType);
            if (result != null) {
                return result;
            }
        } catch (HttpClientErrorException e) {
            log.error(e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException e) {
            throw httpErrorMsg(e);
        } catch (IOException e) {
            log.error("save temp file error");
        } finally {
            //程序结束时，删除临时文件
            if (tempFile != null) {
                try {
                    FileUtils.forceDelete(tempFile);
                } catch (IOException e) {
                    log.error("delete temp file {} error " + tempFile);
                }
            }
        }
        // 返回响应体
        return null;
    }

    /**
     * 保存临时文件 .
     *
     * @Param: [params]
     * @Return: java.io.File
     * @Author: ZhiCong.Lin
     * @Date: 2018/8/14 14:20
     */
    private File saveTempFile(Map<String, Object> params) throws IOException {
        Set<String> keys = params.keySet();
        for (String key : keys) {
            Object object = params.get(key);
            if (object instanceof MultipartFile) {
                MultipartFile multipartFile = (MultipartFile) object;
                // 获取文件名
                String fileName = multipartFile.getOriginalFilename();
                if (fileName == null) {
                    return null;
                }
                File tmpdir = new File(AccessController.doPrivileged(new GetPropertyAction("java.io.tmpdir")));
                File tempFile = new File(tmpdir, fileName);
                multipartFile.transferTo(tempFile);
                params.put(key, new FileSystemResource(tempFile));
                return tempFile;
            }
        }
        return null;
    }

}
