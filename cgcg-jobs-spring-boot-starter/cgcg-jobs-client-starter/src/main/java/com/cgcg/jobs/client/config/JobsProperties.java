package com.cgcg.jobs.client.config;

import com.cgcg.jobs.core.JobsType;
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
@ConfigurationProperties("cgcg.jobs")
public class JobsProperties {

    private int port = 1234;

}
