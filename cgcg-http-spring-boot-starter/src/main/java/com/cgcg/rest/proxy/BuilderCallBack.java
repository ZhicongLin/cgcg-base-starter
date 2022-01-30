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
     * @param method 执行方法
     * @param args   参数
     * @param url    资源链接
     * @param params RestHandle参数信息
     * @return Object 执行返回结果
     * @author zhicong.lin 2018/8/21 13:48
     */
    Object execute(Method method, Object[] args, String url, RestHandle<String, Object> params);
}
