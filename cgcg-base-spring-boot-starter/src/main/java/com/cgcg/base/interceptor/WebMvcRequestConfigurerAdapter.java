package com.cgcg.base.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Request请求的拦截器和过滤器配置
 * Created by zc.lin on 2017/9/8.
 */
@Order
@Configuration
public class WebMvcRequestConfigurerAdapter implements WebMvcConfigurer {

    @Resource
    private IgnoreHandle ignore;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> root = ignore.getRoot();
        if (root == null) {
            root = new ArrayList<>();
        }
        List<String> authIgnore = ignore.getAuthIgnore();
        if (authIgnore == null || authIgnore.isEmpty()) {
            authIgnore = root;
        }
        registry.addInterceptor(new AuthInterceptor()).addPathPatterns("/**").excludePathPatterns(authIgnore);

        List<String> logIgnore = ignore.getLogIgnore();
        if (logIgnore == null || logIgnore.isEmpty()) {
            logIgnore = root;
        }
        registry.addInterceptor(new RequestInterceptor()).addPathPatterns("/**").excludePathPatterns(logIgnore);
    }

}
