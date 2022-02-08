package com.cgcg.context.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Description : IP工具类
 *
 * @author : zc.lin.
 * @version : 2017/10/17.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IpUtils {
    private static final String UNKNOWN = "unknown";

    /**
     * 获取请求的IP地址
     *
     * @param request
     * @return java.lang.String
     * @author zhicong.lin
     * @date 2022/2/8 9:52
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }
}
