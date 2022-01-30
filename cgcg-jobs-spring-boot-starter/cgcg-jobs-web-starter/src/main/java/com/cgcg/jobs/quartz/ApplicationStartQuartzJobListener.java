package com.cgcg.jobs.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.Resource;
/**
 * @author zhicong.lin
 */
@Slf4j
@Configuration
public class ApplicationStartQuartzJobListener implements ApplicationListener<ContextRefreshedEvent>{
    @Resource
    private SchedulerQuartzService schedulerQuartzService;
    @Resource
    private QuartzRunner quartzRunner;
    /**
     * 初始启动quartz
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            schedulerQuartzService.startJob();
            log.info(">>>>>> 任务执行器已经启动 <<<<<<");
            quartzRunner.startJob();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始注入scheduler
     * @return
     * @throws SchedulerException
     */
    @Bean
    public Scheduler scheduler() throws SchedulerException{
        SchedulerFactory schedulerFactoryBean = new StdSchedulerFactory();
        return schedulerFactoryBean.getScheduler(); 
    }

}