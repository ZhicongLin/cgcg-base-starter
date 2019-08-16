package com.cgcg.rest.param;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Rest Map.
 *
 * @Author: ZhiCong.Lin
 * @Create: 2018-08-15 09:43
 */
@Setter
@Getter
public class RestHandle<T, D> extends HashMap<T, D> {

    private String url;

    private String contentType;

    private String accept;

    private Map<String, Object> uriParams = new HashMap<>();

    private StringBuilder parameterUri = new StringBuilder();

    private String bodyString;

    private HttpHeaders headers = new HttpHeaders();

    private HttpMethod httpMethod;

    private File[] files;

    public void addHeader(String key, Object val) {
        this.headers.add(key, String.valueOf(val));
    }

    private HttpHeaders maxHeader;

    public HttpHeaders getHeaders() {
        if (this.maxHeader != null) {
            return maxHeader;
        }
        return this.headers;
    }

}
