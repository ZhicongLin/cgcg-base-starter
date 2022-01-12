package com.cgcg.jobs.web.service;

import com.cgcg.jobs.core.JobsRunCallBack;
import com.cgcg.jobs.model.TaskInfo;
import org.quartz.SchedulerException;

import java.util.List;

public interface TaskInfoService {

    void saveOrUpdateTaskInfo(TaskInfo info) throws SchedulerException;

    List<TaskInfo> findAll();

    void saveRunRecode(TaskInfo taskInfo, JobsRunCallBack callback);
}
