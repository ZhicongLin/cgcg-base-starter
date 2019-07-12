package com.cgcg.rest.annotation;

import com.cgcg.rest.RestRegistryBean;
import com.cgcg.rest.SpringContextHolder;
import com.cgcg.rest.http.RestTemplateConfigure;
import com.cgcg.rest.http.RestTemplateFactory;
import com.cgcg.rest.properties.RestProperties;
import com.cgcg.rest.properties.RestServerProperties;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 启用.
 *
 * @Author: ZhiCong.Lin
 * @Create: 2018-08-09 10:02
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({SpringContextHolder.class, RestTemplateConfigure.class, RestProperties.class, RestServerProperties.class, RestTemplateFactory.class, RestRegistryBean.class})
public @interface EnableRestClients {

    @AliasFor("basePackages")
    String[] value() default {};

    @AliasFor("value")
    String[] basePackages() default {};

    Class<?>[] basePackageClasses() default {};
}
