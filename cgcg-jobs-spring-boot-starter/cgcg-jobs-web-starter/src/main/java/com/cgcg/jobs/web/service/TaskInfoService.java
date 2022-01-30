package com.cgcg.jobs.web.service;

import com.cgcg.jobs.core.JobsRunCallBack;
import com.cgcg.jobs.model.TaskInfo;
import com.cgcg.jobs.model.TaskServer;
import org.quartz.SchedulerException;

import java.util.List;

/**
 * @author zhicong.lin
 */
public interface TaskInfoService {
    /**
     * 保存或者修改任务信息
     *
     * @param info 任务信息
     * @return void
     * @throws SchedulerException
     * @author : zhicong.lin
     * @date : 2022/1/26 15:38
     */
    void saveOrUpdateTaskInfo(TaskInfo info) throws SchedulerException;

    /**
     * 获取全部服务
     *
     * @return java.util.List<com.cgcg.jobs.model.TaskInfo>
     * @author : zhicong.lin
     * @date : 2022/1/26 15:39
     */
    List<TaskInfo> findAll();

    /**
     * 保存运行记录
     *
     * @param taskInfo 任务
     * @param callback 任务执行回调
     * @throws SchedulerException
     * @return java.lang.Long
     * @author : zhicong.lin
     * @date : 2022/1/26 15:40
     */
    Long saveRunRecode(TaskInfo taskInfo, JobsRunCallBack callback) throws SchedulerException;
    /**
     * 修改服务器状态
     *
     * @param id 服务器详情id
     * @param serverId 服务器基本信息id
     * @param status 状态
     * @return com.cgcg.jobs.model.TaskServer
     * @throws SchedulerException
     * @author : zhicong.lin
     * @date : 2022/1/26 15:41
     */
    TaskServer modifyStatus(Long id, Long serverId, Integer status) throws SchedulerException;

    /**
     * 停止服务器执行任务
     *
     * @param info 任务信息
     * @param serverIds 服务器基本信息id列表
     * @return void
     * @throws SchedulerException
     * @author : zhicong.lin
     * @date : 2022/1/26 15:43
     */
    void stopServers(TaskInfo info, List<Long> serverIds) throws SchedulerException;
}
