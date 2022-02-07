package com.cgcg.jobs.client.service.impl;

import com.cgcg.context.SpringContextHolder;
import com.cgcg.jobs.core.IJobsRunner;
import com.cgcg.jobs.core.JobsRunCallBack;
import com.cgcg.jobs.core.MyJobs;
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
        return exeAndGetJobsRunCallBack(bean, jobId, args, startTime, startLongTime);
    }

    /**
     * 执行任务
     *
     * @param jobs  任务
     * @param jobId 任务id
     * @param args  任务参数
     * @return com.cgcg.jobs.core.JobsRunCallBack
     * @author : zhicong.lin
     * @date : 2022/2/7 9:07
     */
    @Override
    public JobsRunCallBack invoke(MyJobs jobs, String jobId, String args) {
        final LocalDateTime startTime = LocalDateTime.now();
        final long startLongTime = System.currentTimeMillis();
        log.info(">>> start {} {}<<<", jobId, startTime);
        return exeAndGetJobsRunCallBack(jobs, jobId, args, startTime, startLongTime);
    }

    private JobsRunCallBack exeAndGetJobsRunCallBack(MyJobs jobs, String jobId, String args, LocalDateTime startTime, long startLongTime) {
        boolean result = false;
        String remark = null;
        try {
            result = jobs.runner(args);
        } catch (Exception e) {
            remark = e.getClass().getName() + ":" + e.getMessage();
            log.error(e.getMessage());
        }
        final LocalDateTime endTime = LocalDateTime.now();
        final long endLongTime = System.currentTimeMillis();
        JobsRunCallBack back = new JobsRunCallBack(startTime, endTime, endLongTime - startLongTime, result, remark);
        log.info(">>> end {} {}<<<", jobId, endTime);
        return back;
    }
}
