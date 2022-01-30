package com.cgcg.jobs.client.service.impl;

import com.cgcg.context.SpringContextHolder;
import com.cgcg.jobs.client.MyJobs;
import com.cgcg.jobs.core.IJobsRunner;
import com.cgcg.jobs.core.JobsRunCallBack;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 执行任务实现类
 *
 * @author zhicong.lin
 */
@Slf4j
@Service
public class JobsRunnerImpl implements IJobsRunner {
    /**
     * 任务执行入口
     *
     * @param jobId jobId
     * @param args  参数
     * @return JobsRunCallBack
     * @author zhicong.lin 2022/1/26
     */
    @Override
    public JobsRunCallBack invoke(String jobId, String args) {
        final LocalDateTime startTime = LocalDateTime.now();
        final long startLongTime = System.currentTimeMillis();
        log.info(">>> start {} {}<<<", jobId, startTime);
        final MyJobs bean = SpringContextHolder.getBean(jobId);
        boolean result = false;
        String remark = null;
        try {
            result = bean.runner(args);
        } catch (Exception e) {
            remark = e.getClass().getName() + ":" + e.getMessage();
            ;
            log.error(e.getMessage());
        }
        final LocalDateTime endTime = LocalDateTime.now();
        final long endLongTime = System.currentTimeMillis();
        log.info(">>> end {} {}<<<", jobId, endTime);
        return new JobsRunCallBack(startTime, endTime, endLongTime - startLongTime, result, remark);
    }
}
