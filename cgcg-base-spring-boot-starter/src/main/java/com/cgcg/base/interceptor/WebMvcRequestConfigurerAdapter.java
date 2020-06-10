package com.cgcg.base.interceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Request请求的拦截器和过滤器配置
 * Created by zc.lin on 2017/9/8.
 */
@Order
@Configuration
public class WebMvcRequestConfigurerAdapter implements WebMvcConfigurer {

    private  static final List<String> DEFAULT_IGNORE = Arrays.asList("/swagger-resources/**", "/swagger-ui.html","/doc.html", "/error", "/webjars/**", "/v2/api-doc", "/v2/api-docs-ext");

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
        authIgnore.addAll(DEFAULT_IGNORE);
        registry.addInterceptor(new AuthInterceptor()).addPathPatterns("/**").excludePathPatterns(authIgnore);

        List<String> logIgnore = ignore.getLogIgnore();
        if (logIgnore == null || logIgnore.isEmpty()) {
            logIgnore = root;
        }
        logIgnore.addAll(DEFAULT_IGNORE);
        registry.addInterceptor(new RequestInterceptor()).addPathPatterns("/**").excludePathPatterns(logIgnore);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/META-INF/resources/").setCachePeriod(0);
    }
}
