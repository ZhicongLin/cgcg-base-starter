package com.cgcg.rest.filter;

import com.cgcg.rest.param.RestHandle;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

/**
 * http请求过滤器.
 *
 * @Author: ZhiCong.Lin
 * @Create: 2018-08-09 17:34
 */
public interface RestFilter {

    /**
     * builder成功，excute前执行 .
     *
     * @param url
     * @param httpMethod
     * @param restHandle
     * @param headers
     * @param returnType
     * @return void
     * @author : zhicong.lin
     * @date : 2022/1/26 16:15
     */
    default void postServer(String url, HttpMethod httpMethod, RestHandle<String, Object> restHandle, HttpHeaders headers, Class<?> returnType) {
    }

    /**
     * excute后执行 .
     *
     * @param result
     * @param returnType
     * @return Object result
     * @author : zhicong.lin
     * @date : 2022/1/26 16:15
     */
    default Object end(Object result, Class<?> returnType) {
        return result;
    }
}
