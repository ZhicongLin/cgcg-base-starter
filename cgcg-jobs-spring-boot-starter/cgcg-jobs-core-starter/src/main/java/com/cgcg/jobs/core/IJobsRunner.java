package com.cgcg.jobs.core;

/**
 * 任务执行器
 *
 * @author zhicong.lin
 */
public interface IJobsRunner {

    /**
     * 任务执行入口
     *
     * @param jobId jobId
     * @param args 参数
     * @return JobsRunCallBack
     * @author zhicong.lin 2022/1/26
     */
    JobsRunCallBack invoke(String jobId, String args);
}
