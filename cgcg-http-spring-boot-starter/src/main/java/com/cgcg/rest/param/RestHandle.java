package com.cgcg.rest.param;

import lombok.Getter;
import lombok.Setter;

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

    private Boolean down;

    private Map<String, Object> uriParams = new HashMap<>();

    private String bodyString;
}
