package com.cgcg.jobs.client.config;

import com.cgcg.jobs.core.IJobsRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.remoting.rmi.RmiServiceExporter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.rmi.RemoteException;

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
}
