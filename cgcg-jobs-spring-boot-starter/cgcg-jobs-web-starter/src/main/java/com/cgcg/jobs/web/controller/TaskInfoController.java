package com.cgcg.jobs.web.controller;

import com.cgcg.jobs.web.service.TaskInfoService;
import io.swagger.annotations.Api;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author zhicong.lin
 */
@Api
@RestController
@RequestMapping("/taskInfos")
public class TaskInfoController {
    @Resource
    private TaskInfoService taskInfoService;

    @PutMapping
    public void put(Long id, Long serverId,Integer status) throws SchedulerException {
        this.taskInfoService.modifyStatus(id, serverId, status);
    }
}
