package com.cgcg.base.swagger;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import com.cgcg.context.SpringContextHolder;

/**
 * swagger配置
 *
 * @author zhicong.lin
 */
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
                // 设置需要被扫描的类，这里设置为添加了@Api注解的类
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    /**
     * 安全模式，这里指定token通过Authorization头请求头传递
     *
     * @return
     */
    private List<SecurityScheme> securitySchemes() {
        List<SecurityScheme> apiKeyList = new ArrayList<>();
        final String header = this.properties.getAuthHeader();
        if (StringUtils.isNotBlank(header)) {
            apiKeyList.add(new ApiKey(header, header, "header"));
        }
        return apiKeyList;
    }

    /**
     * 安全上下文
     */
    private List<SecurityContext> securityContexts() {
        final List<SecurityContext> securityContexts = new ArrayList<>();
        securityContexts.add(SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build());
        return securityContexts;
    }

    /**
     * 默认的安全上引用
     */
    private List<SecurityReference> defaultAuth() {
        final AuthorizationScope[] authorizationScopes = new AuthorizationScope[]{
                new AuthorizationScope("global", "accessEverything")
        };
        final List<String> headers = this.properties.getHeaders();
        if (headers != null) {
            return headers.stream()
                    .map(h -> new SecurityReference(h, authorizationScopes))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }


    private ApiInfo apiInfo() {
        String name = properties.getName();
        if (StringUtils.isBlank(name)) {
            name = SpringContextHolder.getProperty("spring.application.name");
        }
        final Contact contact = new Contact(properties.getContactName(), properties.getContactUrl(), properties.getContactEmail());
        return new ApiInfoBuilder()
                .title(name)
                .description(properties.getDesc())
                .version(properties.getVersion())
                .contact(contact)
                .build();
    }

}
