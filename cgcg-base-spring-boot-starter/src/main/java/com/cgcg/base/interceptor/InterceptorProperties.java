package com.cgcg.base.interceptor;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 拦截器配置.
 *
 * @author zhicong.lin
 * @date 2019/6/25
 */
@Setter
@Getter
@Component
@ConfigurationProperties("cgcg.interceptor")
public class InterceptorProperties {

    private boolean auth;

}
