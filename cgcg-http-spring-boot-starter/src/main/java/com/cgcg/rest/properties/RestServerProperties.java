package com.cgcg.rest.properties;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Rest client properties.
 *
 * @Author: ZhiCong.Lin
 * @Create: 2018-08-08 13:11
 */
@Setter
@Getter
@Component
@ConfigurationProperties("rest.server")
public class RestServerProperties {

    private String host;

    private String port;

    private boolean https = false;

}
