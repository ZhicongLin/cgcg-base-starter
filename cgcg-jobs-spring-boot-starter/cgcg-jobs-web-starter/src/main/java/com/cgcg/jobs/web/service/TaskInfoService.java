package com.cgcg.jobs.web.service;

import com.cgcg.jobs.core.JobsRunCallBack;
import com.cgcg.jobs.model.TaskInfo;
import com.cgcg.jobs.model.TaskServer;
import org.quartz.SchedulerException;

import java.util.List;

public interface TaskInfoService {

    void saveOrUpdateTaskInfo(TaskInfo info) throws SchedulerException;

    List<TaskInfo> findAll();

    Long saveRunRecode(TaskInfo taskInfo, JobsRunCallBack callback) throws SchedulerException;

    TaskServer modifyStatus(Long id, Long serverId, Integer status) throws SchedulerException;

    void stopServers(TaskInfo info, List<Long> serverId) throws SchedulerException;
}
