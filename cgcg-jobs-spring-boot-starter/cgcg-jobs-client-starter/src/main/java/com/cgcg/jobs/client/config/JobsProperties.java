package com.cgcg.jobs.client.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties("cgcg.jobs")
public class JobsProperties {

    private int port = 1234;
}
