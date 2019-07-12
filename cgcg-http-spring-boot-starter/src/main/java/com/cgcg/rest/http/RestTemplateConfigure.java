package com.cgcg.rest.http;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * rest temp conf.
 *
 * @Author: ZhiCong.Lin
 * @Create: 2018-08-08 17:37
 */
@Configuration
public class RestTemplateConfigure {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(new RestInterceptor()));
        return restTemplate;
    }
}
