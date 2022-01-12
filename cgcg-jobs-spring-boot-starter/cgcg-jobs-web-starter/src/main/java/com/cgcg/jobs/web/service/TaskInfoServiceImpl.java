package com.cgcg.jobs.web.service;

import com.alibaba.fastjson.JSON;
import com.cgcg.jobs.core.JobsRunCallBack;
import com.cgcg.jobs.model.TaskInfo;
import com.cgcg.jobs.model.TaskRunRecode;
import com.cgcg.jobs.quartz.SchedulerQuartzService;
import com.cgcg.jobs.web.TaskInfoMapper;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TaskInfoServiceImpl implements TaskInfoService {
    @Resource
    private TaskInfoMapper taskInfoMapper;
    @Resource
    private SchedulerQuartzService schedulerQuartzService;

    @Override
    public void saveOrUpdateTaskInfo(TaskInfo info) throws SchedulerException {
        final Long id = info.getId();
        boolean isRemoveOldTask = false;
        if (id != null) {
            TaskInfo oldInfo=  this.taskInfoMapper.findById(id);
            if (!info.getTaskKey().equals(oldInfo.getTaskKey()) || info.getGroupKey().equals(oldInfo.getGroupKey())) {
                isRemoveOldTask = true;
            }
            final int update = this.taskInfoMapper.update(info);
            if (isRemoveOldTask && update > 1) {
                this.schedulerQuartzService.deleteJob(oldInfo.getTaskKey(), oldInfo.getGroupKey());
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
        return taskInfoMapper.findAllTaskInfo();
    }

    @Override
    public void saveRunRecode(TaskInfo taskInfo, JobsRunCallBack callback) {
        final TaskRunRecode taskRunRecode = new TaskRunRecode();
        taskRunRecode.setTaskId(taskInfo.getId());
        taskRunRecode.setRunTime(callback.getRunTime());
        taskRunRecode.setStartTime(callback.getStartTime());
        taskRunRecode.setEndTime(callback.getEndTime());
        taskRunRecode.setResult(callback.getResult());
        taskRunRecode.setRemark(callback.getRemark() == null ? JSON.toJSONString(callback) : callback.getRemark());
        this.taskInfoMapper.saveRunRecode(taskRunRecode);
    }
}
