package com.cgcg.jobs.client.annotation;

import com.cgcg.jobs.client.JobsBeanDefinitionRegistrar;
import com.cgcg.jobs.client.config.CgcgJobsConfiguration;
import com.cgcg.jobs.client.service.impl.JobsRunnerImpl;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
@Import({JobsBeanDefinitionRegistrar.class, CgcgJobsConfiguration.class, JobsRunnerImpl.class})
public @interface EnableJobsClient {

    String[] packages() default {};
}
