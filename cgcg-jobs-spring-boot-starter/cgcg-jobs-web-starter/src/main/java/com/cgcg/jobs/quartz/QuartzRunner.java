package com.cgcg.jobs.quartz;

import com.cgcg.jobs.model.TaskInfo;
import com.cgcg.jobs.web.service.TaskInfoService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhicong.lin
 */
@Slf4j
@Component
public class QuartzRunner {
    @Resource
    private SchedulerQuartzService schedulerQuartzService;
    @Resource
    private TaskInfoService taskInfoService;

    public void startJob() throws SchedulerException {
        final List<TaskInfo> infos = taskInfoService.findAll();
        for (TaskInfo info : infos) {
            schedulerQuartzService.startTaskInfo(info);
        }
    }

}