package com.cgcg.base.util;

import io.swagger.annotations.ApiOperation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * Description : 接口参数处理
 *
 * @author : zc.lin.
 * @version : 2017/10/17.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequestApiUtils {

    public static String fetchApiOperationValue(Object handler) {
        if (handler instanceof HandlerMethod) {
            final ApiOperation annotation = ((HandlerMethod) handler).getMethodAnnotation(ApiOperation.class);
            return null == annotation ? "" : annotation.value();
        } else if (handler instanceof JoinPoint) {
            MethodSignature signature = (MethodSignature) ((JoinPoint) handler).getSignature();
            ApiOperation annotation = signature.getMethod().getAnnotation(ApiOperation.class);
            return null == annotation ? "" : annotation.value();
        }
        return "";
    }


    public static String fetchParam(HttpServletRequest request) {
        Enumeration<?> parameterNames = request.getParameterNames();
        if (!parameterNames.hasMoreElements()) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();

            while (parameterNames.hasMoreElements()) {
                String param = (String) parameterNames.nextElement();
                String[] values = request.getParameterValues(param);
                sb.append("[").append(param).append(":").append(ArrayUtils.toString(values)).append("]");
            }
            return sb.toString();
        }
    }

}
