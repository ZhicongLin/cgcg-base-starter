package com.cgcg.rest.param;

import com.alibaba.fastjson.JSON;
import com.cgcg.rest.annotation.DinamicaMapping;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * RestParam处理实现.
 *
 * @Author: ZhiCong.Lin
 * @Create: 2018-08-09 09:16
 */
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

    /**
     * 根据方法参数组装请求参数 .
     *
     * @Param: [annotation, param, restParam]
     * @Return: void
     * @Author: ZhiCong Lin
     * @Date: 2018/8/15 9:40
     */
    public void visitor(Annotation annotation, Object param, RestHandle<String, Object> restParam) {
        if (annotation instanceof RequestParam) {
            this.visitor((RequestParam) annotation, param, restParam);
        }
        if (annotation instanceof ModelAttribute) {
            this.visitor(param, restParam);
        }
        if (annotation instanceof RequestBody) {
            this.visitor((RequestBody) annotation, param, restParam);
        }
        if (annotation instanceof RequestPart) {
            this.visitor((RequestPart) annotation, param, restParam);
        }
        if (annotation instanceof PathVariable) {
            restParam.put(((PathVariable) annotation).value(), param);
        }
    }

    private void visitor(RequestParam annotation, Object param, RestHandle<String, Object> restParam) {
        restParam.put(annotation.value(), param.toString());
        restParam.getUriParams().put(annotation.value(), param);
    }

    private void visitor(Object param, RestHandle<String, Object> restParam) {
        try {
            if (param instanceof Map) {
                Map map = (Map) param;
                for (Object key : map.keySet()) {
                    restParam.put(key.toString(), map.get(key));
                }
            }
            restParam.putAll(obj2Map(param));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String visitor(PathVariable annotation, Object param, String path) {
        return path.replace("{" + annotation.value() + "}", param.toString());
    }

    @Override
    public String visitor(DinamicaMapping dinamicaMapping, Object arg, String url) {
        String uri = arg.toString();
        if (uri.startsWith(url)) {
            return uri;
        }
        url = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
        uri = uri.startsWith("/") ? uri.substring(1) : uri;
        return url + "/" + uri;
    }

    private void visitor(RequestBody annotation, Object param, RestHandle<String, Object> restParam) {
        if (param instanceof String) {
            restParam.setBodyString(param.toString());
        } else if (param != null){
            restParam.setBodyString(JSON.toJSONString(param));
        }
        restParam.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
    }

    private void visitor(RequestPart annotation, Object param, RestHandle<String, Object> restParam) {
        restParam.put(StringUtils.isNotBlank(annotation.value()) ? annotation.value() : "file", param);
        restParam.setContentType(MediaType.MULTIPART_FORM_DATA_VALUE);
    }
}
