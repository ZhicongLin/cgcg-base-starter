package com.cgcg.jobs.quartz;

import com.alibaba.fastjson.JSON;
import com.cgcg.context.SpringContextHolder;
import com.cgcg.jobs.core.IJobsRunner;
import com.cgcg.jobs.core.JobsRunCallBack;
import com.cgcg.jobs.model.TaskInfo;
import com.cgcg.jobs.web.service.TaskInfoService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
@Slf4j
@Setter
@Getter
public class SchedulerQuartzJob implements Job {
    private TaskInfo taskInfo;
    private Object bean;
    private Class<?> objectType;
    private JobsRunCallBack callback;

    private void before(JobExecutionContext context) {
        final JobDataMap dataMap = context.getMergedJobDataMap();
        this.taskInfo = JSON.parseObject(dataMap.getString("service"), TaskInfo.class);
        log.info(">>> 任务[{}.{}]开始执行 <<<", taskInfo.getGroupKey(), taskInfo.getTaskKey());
        RmiProxyFactoryBean rmiProxyFactoryBean = new RmiProxyFactoryBean();
        rmiProxyFactoryBean.setServiceUrl(String.format("rmi://%s:%s/IJobsRunner", taskInfo.getHost(), taskInfo.getPort()));
        rmiProxyFactoryBean.setServiceInterface(IJobsRunner.class);
        rmiProxyFactoryBean.setRefreshStubOnConnectFailure(true);
        rmiProxyFactoryBean.afterPropertiesSet();
        bean = rmiProxyFactoryBean.getObject();
        objectType = rmiProxyFactoryBean.getObjectType();
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        before(context);
        final Method method;
        try {
            method = this.objectType.getMethod("invoke", String.class, String.class);
            final Object invoke = method.invoke(bean, this.taskInfo.getTaskKey(), this.taskInfo.getArgs());
            callback = JSON.parseObject(JSON.toJSONString(invoke), JobsRunCallBack.class);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        after();
    }

    private void after() {
        if (callback != null) {
            final TaskInfoService service = SpringContextHolder.getBean(TaskInfoService.class);
            service.saveRunRecode(taskInfo, callback);
        }
        log.info(">>> 任务[{}.{}]执行结果：{}", taskInfo.getGroupKey(), taskInfo.getTaskKey(), callback);
        log.info(">>> 任务[{}.{}]执行完成 <<<", taskInfo.getGroupKey(), taskInfo.getTaskKey());
    }

}