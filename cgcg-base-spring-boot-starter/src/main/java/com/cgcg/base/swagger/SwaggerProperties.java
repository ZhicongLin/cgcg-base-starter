package com.cgcg.base.swagger;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * SWAGGER 配置文件.
 *
 * @author zhicong.lin
 * @date 2019/6/24
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "cgcg.swagger")
public class SwaggerProperties {
    private String name = "接口文档";
    private String desc = "http在线接口文档";
    private String apis = "com";
    private List<String> headers;
    private String version = "1.0.0";
    private boolean disabled = false;
    private String redirectUri = "/";
    private String contactName = "";
    private String contactUrl = "";
    private String contactEmail = "";
}
