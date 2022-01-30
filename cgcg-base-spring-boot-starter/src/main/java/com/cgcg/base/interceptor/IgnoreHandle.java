package com.cgcg.base.interceptor;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zhicong.lin
 * @program cgcg-base-starter
 * @description 忽略请求配置
 * @create 2022-01-26 14:13
 **/
@Setter
@Getter
@Component
@ConfigurationProperties("cgcg.interceptor.ignore")
public class IgnoreHandle {
    private List<String> root;
    private List<String> authIgnore;
    private List<String> logIgnore;
}
