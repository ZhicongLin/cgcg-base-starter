package com.test.base.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Request请求的拦截器和过滤器配置
 * Created by zc.lin on 2017/9/8.
 */
@Configuration
public class WebMvcRequestConfigurerAdapter implements WebMvcConfigurer {

    private static String[] ignore = {"/swagger-resources/**", "/swagger-ui.html", "/error", "/webjars/**"};

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor()).addPathPatterns("/**").excludePathPatterns(ignore);
    }

}
