package com.cgcg.base.interceptor;

import com.alibaba.fastjson.JSON;
import com.cgcg.base.core.enums.CharsetCode;
import com.cgcg.base.interceptor.auth.AuthService;
import com.cgcg.base.language.Translator;
import com.cgcg.context.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * 权限拦截器.
 *
 * @author zhicong.lin
 * @date 2019/6/25
 */
@Slf4j
public class AuthInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final String referer = request.getHeader("Referer");
        if (referer != null && (referer.endsWith("/swagger-ui.html") || referer.endsWith("/doc.html"))) {
            return true;
        }
        final Boolean property = SpringContextHolder.getProperty("cgcg.interceptor.auth", Boolean.class);
        if (property != null && property) {
            AuthService authService = null;
            try {
                authService = SpringContextHolder.getBean(AuthService.class);
            } catch (Exception e) {
                log.warn("Not Found AuthService");
            }
            if (authService != null) {
                if (authService.preHandle(request, response, handler)) {
                    return true;
                } else {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    final HashMap<Object, Object> unauthorized = new HashMap<>();
                    unauthorized.put("code", HttpStatus.UNAUTHORIZED.value());
                    unauthorized.put("message", Translator.toLocale("Unauthorized", "没有访问权限"));
                    response.addHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
                    response.setCharacterEncoding(CharsetCode.forUtf8().name());
                    response.getWriter().write(JSON.toJSONString(unauthorized));
                    return false;
                }
            }
            return super.preHandle(request, response, handler);
        } else {
            return super.preHandle(request, response, handler);
        }
    }
}
