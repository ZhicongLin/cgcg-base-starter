package com.cgcg.base.interceptor;

import com.cgcg.base.util.RequestApiUtils;
import com.cgcg.context.util.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求日志拦截器
 * The type Page interceptor.
 *
 * @author zc.lin
 */
public class RequestInterceptor extends HandlerInterceptorAdapter {
    private static final Logger log = LoggerFactory.getLogger(RequestInterceptor.class);
    private static final Map<String, Logger> LOGGER_MAP = new HashMap<>();
    private static final boolean INFO_ENABLED = log.isInfoEnabled();
    private static final String LOGGER_TIME_FLAG = "_START_TIME";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (INFO_ENABLED) {
            final String apiOperationValue = RequestApiUtils.fetchApiOperationValue(handler);
            final String params = RequestApiUtils.fetchParam(request);
            this.getLogger(handler).info("{} [{}] [{}] => [{}] {}", request.getMethod(), IpUtils.getIpAddress(request), apiOperationValue, request.getRequestURL(), params);
            request.setAttribute(LOGGER_TIME_FLAG, System.currentTimeMillis());
        }
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (INFO_ENABLED) {
            try {
                final long e = Long.parseLong(request.getAttribute(LOGGER_TIME_FLAG).toString());
                this.getLogger(handler).info("END [{}] =>[{}] Time [{}]ms", IpUtils.getIpAddress(request), request.getRequestURL(), System.currentTimeMillis() - e);
            } catch (NullPointerException var4) {
                this.getLogger(handler).info("END [{}] =>[{}] ", IpUtils.getIpAddress(request), request.getRequestURL());
            }
        }
    }

    private Logger getLogger(Object handler) {
        Logger logger = log;
        if (handler instanceof HandlerMethod) {
            final HandlerMethod handlerMethod = (HandlerMethod) handler;
            final String methodName = handlerMethod.getBeanType().getName() + "#" + handlerMethod.getMethod().getName();
            logger = LOGGER_MAP.get(methodName);
            if (logger == null) {
                logger = LoggerFactory.getLogger(methodName);
                LOGGER_MAP.put(methodName, logger);
            }
        }
        return logger;
    }
}
