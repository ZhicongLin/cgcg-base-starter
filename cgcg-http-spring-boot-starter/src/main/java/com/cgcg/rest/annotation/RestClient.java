package com.cgcg.rest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这是一个RestTemplate的注解.
 *
 * @Author: ZhiCong.Lin
 * @Create: 2018-08-08 11:52
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RestClient {
    /**
     * 请求服务配置 .
     *
     * @Param: []
     * @Return: java.lang.String
     * @Author: ZhiCong Lin
     * @Date: 2018/8/10 9:58
     */
    String value() default "";

    /**
     * 绑定路径 .
     *
     * @Param: []
     * @Return: java.lang.String
     * @Author: ZhiCong Lin
     * @Date: 2018/8/21 14:17
     */
    String url() default "";

    /**
     * 抱错回调.
     *
     * @Param: []
     * @Return: java.lang.Class
     * @Author: ZhiCong Lin
     * @Date: 2018/8/10 9:58
     */
    Class fallback() default Void.class;

    /**
     * 是否https协议，默认否 .
     *
     * @Param: []
     * @Return: boolean
     * @Author: ZhiCong Lin
     * @Date: 2018/8/10 9:59
     */
    boolean https() default false;

    /**
     * 默认配置文件模式读取value
     *
     * @return
     */
    boolean properties() default true;
}
