package com.cgcg.base.swagger;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * SWAGGER 配置文件.
 *
 * @author zhicong.lin
 * @date 2019/6/24
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "swagger")
public class SwaggerProperties {
    private String name = "接口文档";
    private String desc = "http在线接口文档";
    private String apis = "com";
    private String version = "1.0.0";
}
