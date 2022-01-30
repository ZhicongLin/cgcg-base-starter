package com.cgcg.base.format;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhicong.lin
 */
@Component
public class FormatConfig {

    @Bean("responseDataIgnore")
    @ConfigurationProperties(prefix = "cgcg.format.response-data-ignore")
    public List<String> getResponseDataIgnore() {
        return new ArrayList<>();
    }
}
