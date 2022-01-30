package com.cgcg.base.interceptor.auth;

import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 鉴权业务逻辑.
 *
 * @author zhicong.lin
 * @date 2019/6/25
 */
public interface AuthService {


    /**
     * 鉴权执行之前
     *
     * @param request
     * @param response
     * @param handler
     * @return boolean
     * @throws Exception
     * @author zhicong.lin 2022/1/26
     */
    default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    /**
     * 鉴权业务执行之后
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     * @author zhicong.lin 2022/1/26
     */
    default void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }
}
