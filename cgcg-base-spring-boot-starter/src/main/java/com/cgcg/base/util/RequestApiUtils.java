package com.cgcg.base.util;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Enumeration;

/**
 * Description :
 *
 * @author : zc.lin.
 * @version : 2017/10/17.
 */
public class RequestApiUtils {


    public static String fetchApiOperationValue(Object handler) {
        if (handler instanceof HandlerMethod) {
            ApiOperation annotation = (ApiOperation) ((HandlerMethod) handler).getMethod().getAnnotation(ApiOperation.class);
            return null == annotation ? "" : annotation.value();
        } else if (handler instanceof JoinPoint) {
            MethodSignature signature = (MethodSignature) ((JoinPoint) handler).getSignature();
            ApiOperation annotation = (ApiOperation) signature.getMethod().getAnnotation(ApiOperation.class);
            return null == annotation ? "" : annotation.value();
        }
        return "";
    }


    public static String fetchApiOperationValue(JoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        ApiOperation annotation = (ApiOperation) signature.getMethod().getAnnotation(ApiOperation.class);
        return null == annotation ? "" : annotation.value();
    }

    public static String fetchParam(HttpServletRequest request) {
        Enumeration parameterNames = request.getParameterNames();
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

    public static String fetchHeadParams(HttpServletRequest request) {
        Enumeration parameterNames = request.getHeaderNames();
        StringBuilder sb = null;

        while (parameterNames.hasMoreElements()) {
            String param = (String) parameterNames.nextElement();
            if (param.contains("custom_")) {
                sb = null == sb ? new StringBuilder() : sb;
                String value = request.getHeader(param);
                sb.append("[").append(param).append(":").append(ArrayUtils.toString(value)).append("]");
            }
        }

        return null == sb ? "" : sb.toString();
    }

    public static String fetchFileParams(HttpServletRequest request, JoinPoint joinPoint) {
        if (!StringUtils.isEmpty(request.getContentType()) && request.getContentType().contains("multipart/")) {
            Object[] objs = joinPoint.getArgs();
            StringBuilder sb = null;
            if (null != objs && 0 != objs.length) {
                Object[] var4 = objs;
                int var5 = objs.length;

                for (int var6 = 0; var6 < var5; ++var6) {
                    Object o = var4[var6];
                    if (o instanceof MultipartFile) {
                        MultipartFile var13 = (MultipartFile) o;
                        sb = null == sb ? new StringBuilder() : sb;
                        sb.append("[").append(var13.getName()).append(":").append(var13.getOriginalFilename()).append("]");
                    } else if (o instanceof MultipartFile[]) {
                        MultipartFile[] mfs = (MultipartFile[]) ((MultipartFile[]) o);
                        sb = null == sb ? new StringBuilder() : sb;
                        MultipartFile[] var9 = mfs;
                        int var10 = mfs.length;

                        for (int var11 = 0; var11 < var10; ++var11) {
                            MultipartFile mf = var9[var11];
                            sb.append("[").append(mf.getName()).append(":").append(mf.getOriginalFilename()).append("]");
                        }
                    }
                }
            }

            return null == sb ? "" : sb.toString();
        } else {
            return "";
        }
    }

    public static String fetchJsonParams(HttpServletRequest request, JoinPoint joinPoint) {
        if (StringUtils.isEmpty(request.getContentType())) {
            return "";
        } else if (!request.getContentType().contains("multipart/") && !request.getContentType().contains("application/json")) {
            return "";
        } else {
            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
            int index = -1;

            for (int i = 0; i < method.getParameterAnnotations().length; ++i) {
                if (method.getParameterAnnotations()[i].length > 0) {
                    for (int j = 0; j < method.getParameterAnnotations()[i].length; ++j) {
                        if (method.getParameterAnnotations()[i][j].annotationType() == RequestBody.class) {
                            index = i;
                            break;
                        }
                    }
                }
            }

            if (index >= 0) {
                return joinPoint.getArgs()[index] instanceof String ? joinPoint.getArgs()[index].toString() : JSON.toJSONString(joinPoint.getArgs()[index]);
            } else {
                return "";
            }
        }
    }
}
