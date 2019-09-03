package com.cgcg.base.interceptor;

import com.cgcg.base.util.RequestApiUtils;
import com.cgcg.context.util.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 请求日志拦截器
 * The type Page interceptor.
 *
 * @author zc.lin
 */
@Slf4j
public class RequestInterceptor extends HandlerInterceptorAdapter {

    private static final boolean INFO_ENABLED = log.isInfoEnabled();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (INFO_ENABLED) {
            String apiOperationValue = RequestApiUtils.fetchApiOperationValue(handler);
            String params = RequestApiUtils.fetchParam(request);
            log.info("{} [{}] [{}] => [{}] {}", request.getMethod(), IpUtils.getIpAddress(request), apiOperationValue, request.getRequestURL(), params);
            request.setAttribute("_START_TIME", System.currentTimeMillis());
        }
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (INFO_ENABLED) {
            try {
                long e = Long.parseLong(request.getAttribute("_START_TIME").toString());
                log.info("END [{}] =>[{}] Time [{}]ms", IpUtils.getIpAddress(request), request.getRequestURL(), System.currentTimeMillis() - e);
            } catch (NullPointerException var4) {
                log.info("END [{}] =>[{}] ", IpUtils.getIpAddress(request), request.getRequestURL());
            }
        }
    }

}
