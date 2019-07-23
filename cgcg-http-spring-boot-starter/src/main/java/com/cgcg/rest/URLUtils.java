package com.cgcg.rest;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class URLUtils {

    public static String clearUrl(String url) {
        if (StringUtils.isNotBlank(url) && url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }

    public static String add(String url, String suffix) {
        return clearUrl(url) + StringUtils.prependIfMissing(suffix, "/");
    }

    public static String addParameter(String url, String uri) {
        if (url.contains("?")) {
            return url + uri;
        }
        return url + uri.replaceFirst("&", "?");
    }
}
