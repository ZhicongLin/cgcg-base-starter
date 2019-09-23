package com.cgcg.rest.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Rest包路径配置.
 *
 * @Author: ZhiCong.Lin
 * @Create: 2018-08-08 14:20
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "cgcg.rest")
public class RestProperties {

    private String scanPackage;
    private int connectTimeout = 60000;
    private int readTimeout = 60000;
    private int connectionRequestTimeout = 60000;
}
