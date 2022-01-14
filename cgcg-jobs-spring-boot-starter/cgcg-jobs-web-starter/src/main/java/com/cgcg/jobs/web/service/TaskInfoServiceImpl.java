package com.cgcg.jobs.web.service;

import com.alibaba.fastjson.JSON;
import com.cgcg.jobs.core.JobsRunCallBack;
import com.cgcg.jobs.model.TaskInfo;
import com.cgcg.jobs.model.TaskRunRecode;
import com.cgcg.jobs.model.TaskServer;
import com.cgcg.jobs.quartz.JobsWebProperties;
import com.cgcg.jobs.quartz.SchedulerQuartzService;
import com.cgcg.jobs.thread.QueueRestartRunner;
import com.cgcg.jobs.thread.QueueStopTaskRunner;
import com.cgcg.jobs.web.mapper.TaskInfoMapper;
import com.cgcg.jobs.web.mapper.TaskServerMapper;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TaskInfoServiceImpl implements TaskInfoService {
    @Resource
    private JobsWebProperties jobsWebProperties;
    @Resource
    private TaskInfoMapper taskInfoMapper;
    @Resource
    private TaskServerMapper taskServerMapper;
    @Resource
    private SchedulerQuartzService schedulerQuartzService;
    @Resource
    private QueueStopTaskRunner queueStopTaskRunner;
    @Resource
    private QueueRestartRunner queueRestartRunner;
    @Override
    public void saveOrUpdateTaskInfo(TaskInfo info) throws SchedulerException {
        final Long id = info.getId();
        boolean isRemoveOldTask = false;
        if (id != null) {
            TaskInfo oldInfo = this.taskInfoMapper.findById(id);
            if (!info.getTaskKey().equals(oldInfo.getTaskKey()) || info.getGroupKey().equals(oldInfo.getGroupKey())) {
                isRemoveOldTask = true;
            }
            final int update = this.taskInfoMapper.update(info);
            if (isRemoveOldTask && update > 0) {
                queueStopTaskRunner.push(oldInfo);
//                this.schedulerQuartzService.deleteJob(oldInfo.getTaskKey(), oldInfo.getGroupKey());
                this.schedulerQuartzService.startTaskInfo(info);
            } else if (!oldInfo.getCron().equals(info.getCron())) {
                this.schedulerQuartzService.modifyJob(info.getTaskKey(), info.getGroupKey(), info.getCron());
            }
        } else {
            this.taskInfoMapper.insert(info);
            this.schedulerQuartzService.startTaskInfo(info);
        }
    }

    @Override
    public List<TaskInfo> findAll() {
        final List<TaskInfo> allTaskInfo = taskInfoMapper.findAllTaskInfo();
        final List<TaskServer> servers = taskServerMapper.findByStatus(1);
        final Map<Long, List<TaskServer>> map = servers.stream().collect(Collectors.groupingBy(TaskServer::getTaskId));
        allTaskInfo.forEach(a -> a.setServers(map.get(a.getId())));
        return allTaskInfo;
    }

    @Override
    public Long saveRunRecode(TaskInfo taskInfo, JobsRunCallBack callback) throws SchedulerException {
        final TaskRunRecode taskRunRecode = new TaskRunRecode();
        taskRunRecode.setTaskId(taskInfo.getId());
        taskRunRecode.setServerId(taskInfo.getServer().getId());
        taskRunRecode.setRunTime(callback.getRunTime());
        taskRunRecode.setStartTime(callback.getStartTime());
        taskRunRecode.setEndTime(callback.getEndTime());
        taskRunRecode.setResult(callback.getResult());
        taskRunRecode.setRemark(callback.getRemark() == null ? JSON.toJSONString(callback) : callback.getRemark());
        this.taskInfoMapper.saveRunRecode(taskRunRecode);
        if (!callback.getResult()) {
            //任务失败，判断是否超次数
            return this.checkAndCloseTask(taskInfo);
        }
        return null;
    }

    @Override
    public TaskServer modifyStatus(Long id, Long serverId, Integer status) throws SchedulerException {
        final List<TaskServer> servers = this.taskServerMapper.findByTaskId(id);
        final TaskServer taskServer = servers.stream().filter(s -> s.getId().equals(serverId)).findFirst().orElse(null);
        if (taskServer == null) {
            return taskServer;
        }
        final Long taskId = taskServer.getTaskId();
        final Integer oldStatus = taskServer.getStatus();
        if (oldStatus == status.intValue()) {
            return taskServer;
        }
        taskServer.setStatus(status);
        this.taskServerMapper.modifyStatus(serverId, status);
        final TaskInfo taskInfo = this.taskInfoMapper.findById(taskId);
        final List<TaskServer> serverList = servers.stream().filter(s -> s.getStatus() == 1).collect(Collectors.toList());
        if (serverList.isEmpty()) {
            // 删除任务
            queueStopTaskRunner.push(taskInfo);
//            this.schedulerQuartzService.deleteJob(taskInfo.getTaskKey(), taskInfo.getGroupKey());
        } else {
            // 重启任务
            taskInfo.setServers(serverList);
            queueRestartRunner.push(taskInfo);
//            this.schedulerQuartzService.resumeJob(taskInfo.getTaskKey(), taskInfo.getGroupKey(), taskInfo);
        }
        return taskServer;
    }

    @Override
    public void stopServers(TaskInfo info, List<Long> serverIds) throws SchedulerException {
        final List<TaskServer> servers = info.getServers();
        final Map<Long, TaskServer> serverMap = servers.stream().collect(Collectors.toMap(TaskServer::getId, k -> k, (k1, k2) -> k2));
        for (Long serverId : serverIds) {
            this.taskServerMapper.modifyStatus(serverId, 0);
            serverMap.remove(serverId);
        }
        //判断是否有服务，有任务则重启
        if (!serverMap.isEmpty()) {
            final Set<Long> keySet = serverMap.keySet();
            List<TaskServer> newServers = new ArrayList<>();
            for (Long key : keySet) {
                newServers.add(serverMap.get(key));
            }
            info.setServers(newServers);
            queueRestartRunner.push(info);
//            this.schedulerQuartzService.resumeJob(info.getTaskKey(), info.getGroupKey(), info);
        } else {
            queueStopTaskRunner.push(info);
//            this.schedulerQuartzService.deleteJob(info.getTaskKey(), info.getGroupKey());
        }
    }

    private Long checkAndCloseTask(TaskInfo taskInfo) throws SchedulerException {
        final int defeatedCount = this.jobsWebProperties.getDefeatedCount();
        if (defeatedCount <= 0) { // 失败不停止， 发起重试
            return taskInfo.getServer().getId();
        }
        final List<TaskRunRecode> tks = this.taskInfoMapper.findRecodeByServerId(taskInfo.getServer().getId(), defeatedCount);
        final long count = tks.stream().filter(TaskRunRecode::getResult).count();
        if (tks.size() == 5 && count == 0) { //大于0表示当前defeatedCount笔数据内有成功的数据，表示未达到暂停任务标准
            final TaskServer server = this.modifyStatus(taskInfo.getId(), taskInfo.getServer().getId(), 2);
            log.warn("任务[{}.{}]已连续失败{}次，暂停向服务[{}:{}]发布执行任务", taskInfo.getGroupKey(),
                    taskInfo.getTaskKey(), defeatedCount, server.getHost(), server.getPort());
            return taskInfo.getServer().getId();
        }
        return taskInfo.getServer().getId();
    }
}
