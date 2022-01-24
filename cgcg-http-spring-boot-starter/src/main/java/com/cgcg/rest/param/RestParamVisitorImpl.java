package com.cgcg.rest.param;

import com.alibaba.fastjson.JSON;
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

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String DEFAULT_FILE_KEY = "file";
    private static final String TMP_DIR = "java.io.tmpdir";

    /**
     * 对象转Map .
     *
     * @Param: [bean]
     * @Return: java.util.Map<java.lang.String, java.lang.Object>
     * @Author: ZhiCong Lin
     * @Date: 2018/8/15 9:40
     */
    private static RestHandle<String, Object> obj2Map(Object bean) throws Exception {
//        Class<?> type = bean.getClass();
        final RestHandle<String, Object> returnMap = new RestHandle<>();
//        BeanInfo beanInfo = Introspector.getBeanInfo(type);

        final PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors();

        for (PropertyDescriptor descriptor : propertyDescriptors) {
            final String propertyName = descriptor.getName();
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
            handle.setMaxHeader((HttpHeaders) param);
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
                Map<?, ?> map = (Map<?, ?>) param;
                final Set<?> keySet = map.keySet();
                for (Object key : keySet) {
                    restParam.put(key.toString(), map.get(key));
                }
            } else {
                restParam.putAll(obj2Map(param));
            }
            if (!MediaType.MULTIPART_FORM_DATA_VALUE.equals(restParam.getContentType())) {
                restParam.setContentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
                restParam.getHeaders().add(CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
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
        restParam.setContentType(MediaType.APPLICATION_JSON_VALUE);
        restParam.getHeaders().add(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }

    public void visitor(RequestPart annotation, Object param, RestHandle<String, Object> restParam) {
        restParam.setContentType(MediaType.MULTIPART_FORM_DATA_VALUE);
        restParam.getHeaders().add(CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE);
        final String fileKey = StringUtils.isNotBlank(annotation.value()) ? annotation.value() : DEFAULT_FILE_KEY;
        this.saveTempFile(restParam, fileKey, param);
    }

    @Override
    public void visitor(DynamicParam annotation, Object param, RestHandle<String, Object> restParam) {
        if (annotation.isUrl()) {
            restParam.setUrl(param.toString());
        } else if (StringUtils.isNotBlank(annotation.value())) {
            restParam.setUrl(URLUtils.add(restParam.getUrl(), annotation.value()));
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
            final MultipartFile multipartFile = (MultipartFile) fileObject;
            // 获取文件名
            final String fileName = multipartFile.getOriginalFilename();
            if (fileName == null) {
                return;
            }
            try {
                final File tmpdir = new File(AccessController.doPrivileged(new GetPropertyAction(TMP_DIR)));
                final File tempFile = new File(tmpdir, fileName);
                multipartFile.transferTo(tempFile);
                params.put(fileKey, new FileSystemResource(tempFile));
                params.setFiles(new File[]{tempFile});
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
