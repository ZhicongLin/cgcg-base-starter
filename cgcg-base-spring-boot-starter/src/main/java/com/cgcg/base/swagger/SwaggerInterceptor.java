package com.cgcg.base.swagger;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Swagger 拦截器
 *
 * @author Caesar Liu
 * @description 用于禁用swagger，当swagger.disabled为true时重定向至${swagger.redirect-uri}
 * @date 2019/4/24 18:23
 */
@Slf4j
@Component
public class SwaggerInterceptor implements HandlerInterceptor {
    private static final String SWAGGER_UI_STR = "swagger-ui";
    @Resource
    SwaggerProperties properties;

    @SneakyThrows
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        final String uri = request.getRequestURI();
        if (uri.contains(SWAGGER_UI_STR)) {
            response.sendRedirect("/doc.html");
            return Boolean.FALSE;
        }
        if (!properties.isDisabled()) {
            return Boolean.TRUE;
        }
        response.sendRedirect(properties.getRedirectUri());
        return Boolean.FALSE;
    }
}
