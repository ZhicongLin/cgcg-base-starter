package com.cgcg.jobs.quartz;

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
@ConfigurationProperties(prefix = "cgcg.jobs")
public class JobsWebProperties {
    /**
     * 失败暂停次数
     */
    private int defeatedCount = 3;
}
