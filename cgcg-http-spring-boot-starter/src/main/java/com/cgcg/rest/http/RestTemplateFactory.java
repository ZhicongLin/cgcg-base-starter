package com.cgcg.rest.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cgcg.rest.URLUtils;
import com.cgcg.rest.exception.ErrorFactory;
import com.cgcg.rest.exception.RestException;
import com.cgcg.rest.param.RestHandle;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

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
            log.error(e.getMessage(), e);
        }
        return returnFileName;
    }

    /**
     * 文件下载
     * @return 二进制流
     */
    public byte[] loadFileByte(RestHandle<String, Object> handle) {
        final String url = URLUtils.addParameter(handle.getUrl(), handle.getParameterUri().toString());
        final HttpEntity<byte[]> requestEntity = new HttpEntity<>(handle.getHeaders());
        try {
            ResponseEntity<byte[]> result = restTemplate.exchange(url, handle.getHttpMethod(), requestEntity, byte[].class, handle.getUriParams());
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
                final String fileName = String.valueOf(handle.getUriParams().get("fileName") == null ? "" : handle.getUriParams().get("fileName"));
                if (response.getHeader("content-disposition") == null) {
                    //3.设置content-disposition响应头控制浏览器以下载的形式打开文件
                    response.setHeader("content-disposition", "attachment; filename=" + encodingFileName(fileName));
                }
                return result.getBody();
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

    public <T> T execute(RestHandle<String, Object> handle,  Class<T> resultType) {
        final String url = URLUtils.addParameter(handle.getUrl(), handle.getParameterUri().toString());
        HttpEntity<?> httpEntity = this.createHttpEntity(handle);
        handle.putAll(handle.getUriParams());
        try {
            return this.executeHttpRequest(url, handle.getHttpMethod(), httpEntity, resultType, handle);
        } finally {
            //程序结束时，删除临时文件
            final File[] files = handle.getFiles();
            if (files != null) {
                try {
                    for (File tempFile : files) {
                        FileUtils.forceDelete(tempFile);
                    }
                } catch (IOException e) {
                    log.error("delete temp file error");
                }
            }
        }
    }

    private HttpEntity<?> createHttpEntity(RestHandle<String, Object> handle) {
        HttpEntity<?> httpEntity;
        final String contentType = handle.getContentType();
        if (MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(contentType) || MediaType.MULTIPART_FORM_DATA_VALUE.equals(contentType)) {
            MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
            final Set<Map.Entry<String, Object>> entrySet = handle.entrySet();
            for (Map.Entry<String, Object> entry : entrySet) {
                multiValueMap.put(entry.getKey(), Collections.singletonList(entry.getValue()));
            }
            httpEntity = new HttpEntity<>(multiValueMap);
        } else if ((MediaType.APPLICATION_JSON_UTF8_VALUE.equals(contentType) || MediaType.APPLICATION_JSON_VALUE.equals(contentType)) && StringUtils.isNotBlank(handle.getBodyString())) {
            httpEntity = new HttpEntity<>(handle.getBodyString(), handle.getHeaders());
        } else {
            httpEntity = new HttpEntity<>(handle.getHeaders());
        }
        return httpEntity;
    }
    /**
     * @return RestException
     * @Description: 捕获微服务接口中返回的异常信息, 封装后再抛出
     * @author nengneng.lian
     * @date 2017/11/15 10:50
     */
    private RestException httpErrorMsg(HttpServerErrorException e) {
        log.error("服务异常原始信息：{}" , e.getResponseBodyAsString(), e);
        // 异常码
        int errorCode = e.getRawStatusCode();
        // 异常消息
        String errorMsg = e.getMessage();
        // 获取异常body
        if (StringUtils.isNotBlank(e.getResponseBodyAsString())) {
            JSONObject jsonError = JSON.parseObject(e.getResponseBodyAsString());
            // errorCode存在且为数字
            if (jsonError.get("errorCode") != null && StringUtils.isNumeric(jsonError.get("errorCode").toString())) {
                errorCode = Integer.parseInt(jsonError.get("errorCode").toString()) ;
            } else if (jsonError.get("code") != null && StringUtils.isNumeric(jsonError.get("code").toString())) {
                errorCode = Integer.parseInt(jsonError.get("code").toString()) ;
            }
            // errorMsg存在
            if (jsonError.get("message") != null) { // 微服务封装errorMsg的情况下选择errorMsg
                errorMsg = jsonError.get("message").toString();
            } else if (jsonError.get("errorMsg") != null) { // 微服务没有封装则调用SpringBoot自带异常消息
                errorMsg = jsonError.get("errorMsg").toString();
            }
        }
        // 抛出异常
        return new RestException(errorCode, errorMsg);
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
                throw new RestException(statusCode.value(), responseBodyAsString);
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

}
