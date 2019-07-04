package com.cgcg.base.swagger;

import com.cgcg.base.core.context.SpringContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.Resource;

@EnableSwagger2
@Configuration
public class SwaggerConfig {
    @Resource
    private SwaggerProperties properties;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(properties.getApis()))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        String name = properties.getName();
        if (StringUtils.isBlank(name)) {
            name = SpringContextHolder.getProperty("spring.application.name");
        }
        return new ApiInfoBuilder()
                .title(name)
                .description(properties.getDesc())
                .version(properties.getVersion())
                .build();
    }
}
