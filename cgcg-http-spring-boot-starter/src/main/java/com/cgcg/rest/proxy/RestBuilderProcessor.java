package com.cgcg.rest.proxy;

import com.cgcg.rest.SpringContextHolder;
import com.cgcg.rest.annotation.LoadMapping;
import com.cgcg.rest.http.RestBuilder;
import com.cgcg.rest.http.RestTemplateFactory;
import com.cgcg.rest.param.RestHandle;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestPart;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 代理处理器.
 *
 * @Author: ZhiCong.Lin
 * @Create: 2018-08-21 13:49
 */
public class RestBuilderProcessor implements BuilderCallBack {

    private static RestBuilderProcessor restBuilderProcessor = new RestBuilderProcessor();

    /**
     * 包装调用方法：进行预处理、调用后处理
     */
    static Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) {
        return invoke(proxy, method, args);
    }

    /**
     * 包装调用方法：进行预处理、调用后处理
     */
    static Object invoke(Object proxy, Method method, Object[] args) {
        return new RestBuilder(method, args).bulid(restBuilderProcessor);
    }

    private static boolean isFileUpload(Method method, HttpMethod httpMethod, String url) {
        if (httpMethod.equals(HttpMethod.POST)) {
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for (Annotation[] parameterAnnotation : parameterAnnotations) {
                for (Annotation annotation : parameterAnnotation) {
                    if (annotation instanceof RequestPart) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 具体发起请求的方法 .
     *
     * @param method
     * @param args
     * @param url
     * @param httpMethod
     * @param returnType
     * @Param: [method, args, serverUri, httpMethod, headers, values, returnType]
     * @Return: java.lang.Object
     * @Author: ZhiCong Lin
     * @Date: 2018/8/21 13:48
     */
    @Override
    public Object execute(Method method, Object[] args, String url, HttpMethod httpMethod, RestHandle<String, Object> params, HttpHeaders httpHeaders, Class<?> returnType) {
        final RestTemplateFactory templeFactory = SpringContextHolder.getBean(RestTemplateFactory.class);
        if (isFileUpload(method, httpMethod, url)) {
            return templeFactory.uploadFile(url, params, httpHeaders, returnType);
        } else if (method.getAnnotation(LoadMapping.class) != null) {
            params.setDown(method.getAnnotation(LoadMapping.class).down());
            return templeFactory.loadFileByte(url, params, httpHeaders);
        }
        return templeFactory.execute(url, httpMethod, params, httpHeaders, returnType);
    }
}
