package com.cgcg.rest;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhicong.lin
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UrlUtils {

    private static final String WH = "?";
    private static final String ZXG = "/";

    public static String clearUrl(String url) {
        if (StringUtils.isNotBlank(url) && url.endsWith(ZXG)) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }

    public static String add(String url, String suffix) {
        return clearUrl(url) + StringUtils.prependIfMissing(suffix, ZXG);
    }

    public static String addParameter(String url, String uri) {
        if (url.contains(WH)) {
            return url + uri;
        }
        return url + uri.replaceFirst("&", WH);
    }
}
