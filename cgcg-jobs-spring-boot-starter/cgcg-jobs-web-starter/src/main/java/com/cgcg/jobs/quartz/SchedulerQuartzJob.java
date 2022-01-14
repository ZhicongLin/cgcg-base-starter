package com.cgcg.jobs.quartz;

import com.alibaba.fastjson.JSON;
import com.cgcg.context.SpringContextHolder;
import com.cgcg.jobs.core.IJobsRunner;
import com.cgcg.jobs.core.JobsRunCallBack;
import com.cgcg.jobs.model.TaskInfo;
import com.cgcg.jobs.model.TaskRunRecode;
import com.cgcg.jobs.model.TaskServer;
import com.cgcg.jobs.web.mapper.TaskInfoMapper;
import com.cgcg.jobs.web.service.TaskInfoService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.quartz.*;
import org.springframework.remoting.RemoteLookupFailureException;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Setter
@Getter
public class SchedulerQuartzJob implements Job {
    public static Vector<Long> runningKey = new Vector<>();
    private TaskInfo taskInfo;
    private Object bean;
    private Class<?> objectType;
    private JobsRunCallBack callback;
    private List<Long> removeIds = new ArrayList<>();
    private void before() throws RemoteLookupFailureException {
//        final JobDataMap dataMap = context.getMergedJobDataMap();
//        this.taskInfo = JSON.parseObject(dataMap.getString("service"), TaskInfo.class);
//        runningKey.add(this.taskInfo.getId());
        this.weightRandom();
        log.info(">>> 任务[{}.{}]开始由[{}:{}]执行 <<<", taskInfo.getGroupKey(),
                taskInfo.getTaskKey(), taskInfo.getServer().getHost(), taskInfo.getServer().getPort());
        this.createRmiProxy();
    }

    // 多个服务器时，进行权重随机
    private void weightRandom() {
        final List<TaskServer> servers = this.taskInfo.getServers();
        if (servers.isEmpty()) {
            return;
        }
        if (servers.size() == 1) {
            this.taskInfo.setServer(servers.get(0));
            return;
        }
        int total = servers.stream().filter(i -> !removeIds.contains(i.getId())).mapToInt(TaskServer::getCoreCount).sum();
        final int i = RandomUtils.nextInt(0, total) + 1;
        int now = 0;
        for (TaskServer server : servers) {
            if (removeIds.contains(server.getId())) {
                continue;
            }
            if (now + server.getCoreCount() >= i) {
                this.taskInfo.setServer(server);
                break;
            }
            now += server.getCoreCount();
        }
    }

    private void createRmiProxy() {
        RmiProxyFactoryBean rmiProxyFactoryBean = new RmiProxyFactoryBean();
        final TaskServer server = this.taskInfo.getServer();
        rmiProxyFactoryBean.setServiceUrl(String.format("rmi://%s:%s/IJobsRunner", server.getHost(), server.getPort()));
        rmiProxyFactoryBean.setServiceInterface(IJobsRunner.class);
        rmiProxyFactoryBean.setRefreshStubOnConnectFailure(true);
        rmiProxyFactoryBean.afterPropertiesSet();
        this.bean = rmiProxyFactoryBean.getObject();
        objectType = rmiProxyFactoryBean.getObjectType();
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        final JobDataMap dataMap = context.getMergedJobDataMap();
        this.taskInfo = JSON.parseObject(dataMap.getString("service"), TaskInfo.class);
        if (runningKey.contains(this.taskInfo.getId())) {
            log.info(">>> 任务[{}.{}]正在运行中，本次任务取消 <<<", taskInfo.getGroupKey(), taskInfo.getTaskKey());
        } else {
            runningKey.add(this.taskInfo.getId());
            invokeExe(context);
        }
    }

    private void invokeExe(JobExecutionContext context) throws JobExecutionException {
        final Method method;
        final LocalDateTime start = LocalDateTime.now();
        final long startLong = System.currentTimeMillis();
        boolean connectError = false;
        try {
            before();
            method = this.objectType.getMethod("invoke", String.class, String.class);
            final Object invoke = method.invoke(bean, this.taskInfo.getTaskKey(), this.taskInfo.getArgs());
            callback = JSON.parseObject(JSON.toJSONString(invoke), JobsRunCallBack.class);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (RemoteLookupFailureException e) {
            callback = new JobsRunCallBack(start, LocalDateTime.now(), System.currentTimeMillis() - startLong, false, e.getMessage());
            connectError = true;
        } finally {
            after();
            if (connectError) {
                checkRetry();
                invokeExe(context);
            } else {
                log.info(">>> 任务[{}.{}]执行完成 <<<", taskInfo.getGroupKey(), taskInfo.getTaskKey());
                if (!removeIds.isEmpty()) {
                    final TaskInfoService taskInfoService = SpringContextHolder.getBean(TaskInfoService.class);
                    try {
                        taskInfoService.stopServers(taskInfo, removeIds);
                    } catch (SchedulerException e) {
                        log.error(e.getMessage(), e);
                    }
                }
                removeIds = new ArrayList<>();
                runningKey.remove(this.taskInfo.getId());
            }
        }
    }

    private void checkRetry() {
        final JobsWebProperties properties = SpringContextHolder.getBean(JobsWebProperties.class);
        final int defeatedCount = properties.getDefeatedCount();
        final TaskInfoMapper taskInfoMapper = SpringContextHolder.getBean(TaskInfoMapper.class);
        final List<TaskRunRecode> tks = taskInfoMapper.findRecodeByServerId(taskInfo.getServer().getId(), defeatedCount);
        final long count = tks.stream().filter(TaskRunRecode::getResult).count();
        if (tks.size() == 5 && count == 0) { //大于0表示当前defeatedCount笔数据内有成功的数据，表示未达到暂停任务标准
            removeIds.add(taskInfo.getServer().getId());
        }
    }

    private void after() {
        if (callback != null) {
            log.info(">>> 任务[{}.{}]执行结果：{}", taskInfo.getGroupKey(), taskInfo.getTaskKey(), callback);
            final TaskInfoService service = SpringContextHolder.getBean(TaskInfoService.class);
            try {
                service.saveRunRecode(taskInfo, callback);
            } catch (SchedulerException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

}