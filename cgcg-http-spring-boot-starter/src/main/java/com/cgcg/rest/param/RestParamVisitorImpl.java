package com.cgcg.rest.param;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cgcg.rest.URLUtils;
import com.cgcg.rest.annotation.DynamicParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.security.action.GetPropertyAction;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.security.AccessController;
import java.util.Map;
import java.util.Set;

/**
 * RestParam处理实现.
 *
 * @Author: ZhiCong.Lin
 * @Create: 2018-08-09 09:16
 */
@Slf4j
public class RestParamVisitorImpl implements RestParamVisitor {
    /**
     * 对象转Map .
     *
     * @Param: [bean]
     * @Return: java.util.Map<java.lang.String, java.lang.Object>
     * @Author: ZhiCong Lin
     * @Date: 2018/8/15 9:40
     */
    private static RestHandle<String, Object> obj2Map(Object bean) throws Exception {
        Class<?> type = bean.getClass();
        RestHandle<String, Object> returnMap = new RestHandle<>();
        BeanInfo beanInfo = Introspector.getBeanInfo(type);

        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor descriptor : propertyDescriptors) {
            String propertyName = descriptor.getName();
            if (!"class".equals(propertyName)) {
                returnMap.put(propertyName, descriptor.getReadMethod().invoke(bean));
            }
        }
        return returnMap;
    }

    public void visitor(RequestHeader requestHeader, Object param, RestHandle<String, Object> handle) {
        final String value = requestHeader.value();
        if (StringUtils.isNotBlank(value)) {
            handle.addHeader(value, param.toString());
        } else if (param instanceof HttpHeaders) {
            final HttpHeaders headers = (HttpHeaders) param;
            final Set<String> keySet = headers.keySet();
            for (String key : keySet) {
                handle.addHeader(key, headers.get(key));
            }
        } else {
            final JSONObject object = JSON.parseObject(JSON.toJSONString(param));
            final Set<String> keySet = object.keySet();
            for (String key : keySet) {
                handle.addHeader(key, String.valueOf(object.get(key)));
            }
        }
    }

    public void visitor(RequestParam annotation, Object param, RestHandle<String, Object> restParam) {
        restParam.getParameterUri().append("&").append(annotation.value()).append("=").append("{").append(annotation.value()).append("}");
        restParam.getUriParams().put(annotation.value(), param);
    }

    public void visitor(ModelAttribute modelAttribute, Object param, RestHandle<String, Object> restParam) {
        try {
            final String value = modelAttribute.value();
            if (StringUtils.isNotBlank(value)) {
                restParam.put(value, param);
            } else if (param instanceof Map) {
                Map map = (Map) param;
                final Set keySet = map.keySet();
                for (Object key : keySet) {
                    restParam.put(key.toString(), map.get(key));
                }
            } else {
                restParam.putAll(obj2Map(param));
            }
            if (!MediaType.MULTIPART_FORM_DATA_VALUE.equals(restParam.getContentType())) {
                restParam.setContentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
                restParam.getHeaders().add("content-type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void visitor(PathVariable annotation, Object param, RestHandle<String, Object> restParam) {
        restParam.getUriParams().put(annotation.value(), param.toString());
    }

    public void visitor(RequestBody annotation, Object param, RestHandle<String, Object> restParam) {
        if (param instanceof String) {
            restParam.setBodyString(param.toString());
        } else if (param != null) {
            restParam.setBodyString(JSON.toJSONString(param));
        }
        restParam.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        restParam.getHeaders().add("content-type", MediaType.APPLICATION_JSON_UTF8_VALUE);
    }

    public void visitor(RequestPart annotation, Object param, RestHandle<String, Object> restParam) {
        restParam.setContentType(MediaType.MULTIPART_FORM_DATA_VALUE);
        restParam.getHeaders().add("content-type", MediaType.MULTIPART_FORM_DATA_VALUE);
        final String fileKey = StringUtils.isNotBlank(annotation.value()) ? annotation.value() : "file";
        this.saveTempFile(restParam, fileKey, param);
    }

    @Override
    public void visitor(DynamicParam annotation, Object param, RestHandle<String, Object> restParam) {
        boolean isUrl = annotation.isUrl();
        if (isUrl) {
            restParam.setUrl(param.toString());
        } else {
            final String value = annotation.value();
            if (StringUtils.isNotBlank(value)) {
                restParam.setUrl(URLUtils.add(restParam.getUrl(), value));
            }
        }
    }

    /**
     * 保存临时文件 .
     *
     * @Param: [params]
     * @Return: java.io.File
     * @Author: ZhiCong.Lin
     * @Date: 2018/8/14 14:20
     */
    private void saveTempFile(RestHandle<String, Object> params, String fileKey, Object fileObject) {
        if (fileObject instanceof MultipartFile) {
            MultipartFile multipartFile = (MultipartFile) fileObject;
            // 获取文件名
            String fileName = multipartFile.getOriginalFilename();
            if (fileName == null) {
                return;
            }
            try {
                File tmpdir = new File(AccessController.doPrivileged(new GetPropertyAction("java.io.tmpdir")));
                File tempFile = new File(tmpdir, fileName);
                multipartFile.transferTo(tempFile);
                params.put(fileKey, new FileSystemResource(tempFile));
                params.setFiles(new File[]{tempFile});
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
