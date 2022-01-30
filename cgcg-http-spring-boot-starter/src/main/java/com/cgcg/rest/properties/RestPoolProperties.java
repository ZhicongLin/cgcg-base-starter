package com.cgcg.rest.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhicong.lin
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "cgcg.rest.pool")
public class RestPoolProperties {
    private int maxTotal = 1000;
    private int maxPerRoute = 5000;
    private int validateAfterInactivity = 3000;
}