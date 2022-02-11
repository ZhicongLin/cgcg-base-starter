package com.cgcg.jobs.client.config;

import com.cgcg.context.SpringContextHolder;
import com.cgcg.jobs.core.Constant;
import com.cgcg.jobs.core.IJobsRunner;
import com.cgcg.jobs.core.MyJobs;
import lombok.extern.slf4j.Slf4j;
import org.cgcg.redis.core.annotation.Rmqc;
import org.cgcg.redis.core.entity.AbstractRedisTask;
import org.cgcg.redis.core.mq.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.remoting.rmi.RmiServiceExporter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * @author zhicong.lin
 */
@Slf4j
@Component
public class CgcgJobsConfiguration {
    @Resource
    private IJobsRunner iJobsRunner;
    @Resource
    private JobsProperties jobsProperties;

    @Bean
    public RmiServiceExporter rmiServiceExporter() {
        final RmiServiceExporter service = new RmiServiceExporter();
        service.setService(iJobsRunner);
        service.setServiceName("IJobsRunner");
        service.setServiceInterface(IJobsRunner.class);
        service.setRegistryPort(jobsProperties.getPort());
        try {
            service.afterPropertiesSet();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        log.info(">>> Start IJobsRunner Open Rmi Port:{} <<<", jobsProperties.getPort());
        return service;
    }


    @Rmqc(Constant.MQ_ID)
    public void invokeService(Message message) {
        final Map<String, Object> dataMap = message.dataMap();
        final String jobId = dataMap.get("jobId").toString();
        MyJobs bean = null;
        try {
            bean = SpringContextHolder.getBean(jobId);
        } catch (Exception e) {
            log.debug("不执行非当前服务的任务操作[{}]", jobId);
        }
        if (bean != null) {
            final MyJobs finalBean = bean;
            AbstractRedisTask.execute(() -> {
                final String args = dataMap.get("args").toString();
                iJobsRunner.invoke(finalBean, jobId, args);
            });
        }
    }

}
