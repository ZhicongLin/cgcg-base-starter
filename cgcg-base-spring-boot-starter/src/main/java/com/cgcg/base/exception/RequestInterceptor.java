package com.cgcg.base.exception;

import com.cgcg.base.util.IpUtils;
import com.cgcg.base.util.RequestApiUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 分页拦截器
 * The type Page interceptor.
 *
 * @author zc.lin
 */
@Slf4j
public class RequestInterceptor extends HandlerInterceptorAdapter {

    private static final boolean logEnabled = log.isInfoEnabled();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (logEnabled && !request.getRequestURI().contains("/swagger-resources")) {
            this.preLog(request, handler);
        }
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
        if (logEnabled && !request.getRequestURI().contains("/swagger-resources")) {
            try {
                long e = Long.parseLong(request.getAttribute("_START_TIME").toString());
                log.info("response [{}] =>[{}] [{}] cost[{}]ms", IpUtils.getIpAddress(request), request.getMethod(), request.getRequestURL(), System.currentTimeMillis() - e);
            } catch (NullPointerException var4) {
                log.info("response [{}] =>[{}] [{}]", IpUtils.getIpAddress(request), request.getMethod(), request.getRequestURL());
            }
        }
    }

    private void preLog(HttpServletRequest request, Object handler) {
        String apiOperationValue = RequestApiUtils.fetchApiOperationValue(handler);
        String params = RequestApiUtils.fetchParam(request);
        log.info("request [{}] [{}] => [{}] [{}] {}", IpUtils.getIpAddress(request), apiOperationValue, request.getMethod(), request.getRequestURL(), params);
        request.setAttribute("_START_TIME", System.currentTimeMillis());
    }
}
