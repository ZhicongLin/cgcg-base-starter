package com.cgcg.base.interceptor;

import com.alibaba.fastjson.JSON;
import com.cgcg.base.Constant;
import com.cgcg.base.auth.AuthService;
import com.cgcg.base.context.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * .
 *
 * @author zhicong.lin
 * @date 2019/6/25
 */
@Slf4j
public class AuthInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final Boolean property = SpringContextHolder.getProperty("cgcg.interceptor.auth", Boolean.class);
        if (property != null && property) {
            final AuthService authService = SpringContextHolder.getBean(AuthService.class);
            if (authService != null) {
                if (authService.preHandle(request, response, handler)) {
                    return true;
                } else {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    final HashMap<Object, Object> unauthorized = new HashMap<>();
                    unauthorized.put("errorCode", HttpStatus.UNAUTHORIZED);
                    unauthorized.put("errorMsg", "没有访问权限");
                    response.setCharacterEncoding(Constant.CHARTER);
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
