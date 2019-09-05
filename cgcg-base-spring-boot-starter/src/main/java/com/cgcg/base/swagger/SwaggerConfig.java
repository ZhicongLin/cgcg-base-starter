package com.cgcg.base.swagger;

import com.cgcg.context.SpringContextHolder;
import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.PathProvider;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.AbstractPathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@EnableSwagger2
@Configuration
@EnableSwaggerBootstrapUI
public class SwaggerConfig {
    @Resource
    private SwaggerProperties properties;

    @Bean
    public Docket createRestApi() {
        List<Parameter> pars = new ArrayList<>();
        final List<String> headers = this.properties.getHeaders();
        if (headers != null && headers.size() > 0) {
            for (String header : headers) {
                ParameterBuilder tokenPar = new ParameterBuilder();
                tokenPar.name(header).description("Request Header")
                        .modelRef(new ModelRef("string"))
                        .parameterType("header")
                        .required(false).build();
                pars.add(tokenPar.build());
            }
        }
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                // 设置需要被扫描的类，这里设置为添加了@Api注解的类
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.any())
                .build().pathProvider(pathProvider())
                .globalOperationParameters(pars);
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

    private PathProvider pathProvider() {
        return new AbstractPathProvider() {
            @Override
            protected String applicationPath() {
                return "/";
            }

            @Override
            protected String getDocumentationPath() {
                return "/";
            }
        };
    }
}
