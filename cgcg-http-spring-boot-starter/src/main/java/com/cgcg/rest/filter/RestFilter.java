package com.cgcg.rest.filter;

import com.cgcg.rest.param.RestHandle;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.lang.reflect.Method;

/**
 * http请求过滤器.
 *
 * @Author: ZhiCong.Lin
 * @Create: 2018-08-09 17:34
 */
public interface RestFilter {

    /**
     * rest 在builder前执行 .
     *
     * @Param: [method, args]
     * @Return: boolean
     * @Author: ZhiCong Lin
     * @Date: 2018/8/21 17:22
     */
    default boolean pre(Method method, Object[] args) {
        return true;
    }

    /**
     * builder成功，excute前执行 .
     *
     * @Param: [url, httpMethod, restHandle, headers, returnType]
     * @Return: void
     * @Author: ZhiCong Lin
     * @Date: 2018/8/21 17:22
     */
    default void postServer(String url, HttpMethod httpMethod, RestHandle<String, Object> restHandle, HttpHeaders headers, Class<?> returnType) {
    }

    /**
     * excute后执行 .
     *
     * @Param: [result]
     * @Return: java.lang.Object
     * @Author: ZhiCong Lin
     * @Date: 2018/8/21 17:22
     */
    default Object end(Object result, Class<?> returnType) {
        return result;
    }
}
