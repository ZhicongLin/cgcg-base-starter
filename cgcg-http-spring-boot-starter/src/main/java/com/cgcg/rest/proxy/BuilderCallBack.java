package com.cgcg.rest.proxy;

import com.cgcg.rest.param.RestHandle;

import java.lang.reflect.Method;

/**
 * 创建Rest请求的回调.
 *
 * @Author: ZhiCong.Lin
 * @Create: 2018-08-21 13:46
 */
public interface BuilderCallBack {

    /**
     * 具体发起请求的方法 .
     *
     * @Param: [method, args, serverUri, httpMethod, headers, values, returnType]
     * @Return: java.lang.Object
     * @Author: ZhiCong Lin
     * @Date: 2018/8/21 13:48
     */
    Object execute(Method method, Object[] args, String url, RestHandle<String, Object> params);
}
