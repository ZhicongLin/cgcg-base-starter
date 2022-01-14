package com.cgcg.jobs.quartz;

import com.alibaba.fastjson.JSON;
import com.cgcg.jobs.model.TaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SchedulerQuartzService {

    // 任务调度
    @Resource
    private Scheduler scheduler;

    public void startTaskInfo(TaskInfo info) throws SchedulerException {
        CronTrigger cronTrigger = startSimpleTaskInfo(info);
        final List<String> collect = info.getServers().stream().map(i -> i.getHost() + ":" + i.getPort()).collect(Collectors.toList());
        log.info(">>> 注册定时任务[{}.{}]发现服务{},下次执行时间{} <<<", info.getGroupKey(), info.getTaskKey(), JSON.toJSONString(collect), cronTrigger.getNextFireTime());
    }

    private CronTrigger startSimpleTaskInfo(TaskInfo info) throws SchedulerException {
        // 通过JobBuilder构建JobDetail实例，JobDetail规定只能是实现Job接口的实例
        // JobDetail 是具体Job实例
        JobDetail jobDetail = JobBuilder.newJob(SchedulerQuartzJob.class).withIdentity(info.getTaskKey(), info.getGroupKey()).build();
        jobDetail.getJobDataMap().put("service", JSON.toJSONString(info));
        // 基于表达式构建触发器
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(info.getCron());
        // CronTrigger表达式触发器 继承于Trigger
        // TriggerBuilder 用于构建触发器实例
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(info.getTaskKey(), info.getGroupKey())
                .withSchedule(cronScheduleBuilder).build();
        scheduler.scheduleJob(jobDetail, cronTrigger);
        return cronTrigger;
    }

    /**
     * 开始执行所有任务
     *
     * @throws SchedulerException
     */
    public void startJob() throws SchedulerException {
        scheduler.start();
    }

    /**
     * 获取Job信息
     *
     * @param name
     * @param group
     * @return
     * @throws SchedulerException
     */
    public String getJobInfo(String name, String group) throws SchedulerException {
        TriggerKey triggerKey = new TriggerKey(name, group);
        CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        return String.format("time:%s,state:%s", cronTrigger.getCronExpression(),
                scheduler.getTriggerState(triggerKey).name());
    }

    /**
     * 修改某个任务的执行时间
     *
     * @param name
     * @param group
     * @param cron
     * @return
     * @throws SchedulerException
     */
    public boolean modifyJob(String name, String group, String cron) throws SchedulerException {
        Date date = null;
        TriggerKey triggerKey = new TriggerKey(name, group);
        CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        String oldTime = cronTrigger.getCronExpression();
        if (!oldTime.equalsIgnoreCase(cron)) {
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(name, group)
                    .withSchedule(cronScheduleBuilder).build();
            date = scheduler.rescheduleJob(triggerKey, trigger);
            log.info(">>> 修改定时任务[{}.{}],下次执行时间{} <<<", group, name, cronTrigger.getNextFireTime());
        }
        return date != null;
    }

    /**
     * 暂停所有任务
     *
     * @throws SchedulerException
     */
    public void pauseAllJob() throws SchedulerException {
        scheduler.pauseAll();
        log.info(">>> 暂停所有任务 <<<");
    }

    /**
     * 暂停某个任务
     *
     * @param name
     * @param group
     * @throws SchedulerException
     */
    public void pauseJob(String name, String group) throws SchedulerException {
        JobKey jobKey = new JobKey(name, group);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail != null) {
            scheduler.pauseJob(jobKey);
        }
        log.info(">>> 暂停任务[{}.{}] <<<", group, name);
    }

    /**
     * 恢复所有任务
     *
     * @throws SchedulerException
     */
    public void resumeAllJob() throws SchedulerException {
        scheduler.resumeAll();
        log.info(">>> 暂停所有任务 <<<");
    }

    /**
     * 恢复某个任务
     *
     * @param name
     * @param group
     * @throws SchedulerException
     */
    public void resumeJob(String name, String group) throws SchedulerException {
        JobKey jobKey = new JobKey(name, group);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null)
            return;
        scheduler.resumeJob(jobKey);
        log.info(">>> 恢复任务[{}.{}] <<<", group, name);
    }
    /**
     * 恢复某个任务
     *
     * @param name
     * @param group
     * @throws SchedulerException
     */
    public void resumeJob(String name, String group, TaskInfo taskInfo) throws SchedulerException {
        JobKey jobKey = new JobKey(name, group);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null) {
            startTaskInfo(taskInfo);
        } else {
            scheduler.deleteJob(jobKey);
            startSimpleTaskInfo(taskInfo);
            final List<String> collect = taskInfo.getServers().stream().map(i -> i.getHost() + ":" + i.getPort()).collect(Collectors.toList());
            log.info(">>> 重启任务[{}.{}] 发现服务{}<<<", group, name, JSON.toJSONString(collect));
        }
    }

    /**
     * 删除某个任务
     *
     * @param name
     * @param group
     * @throws SchedulerException
     */
    public void deleteJob(String name, String group) throws SchedulerException {
        final JobKey jobKey = new JobKey(name, group);
        final JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail != null) {
            scheduler.deleteJob(jobKey);
            log.info(">>> 删除任务[{}.{}] <<<", group, name);
        } else {
            log.info(">>> 任务[{}.{}]不存在，无需删除 <<<", group, name);
        }
    }
}
