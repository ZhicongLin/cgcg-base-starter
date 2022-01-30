package com.cgcg.jobs.client;

/**
 * 任务统一接口类
 *
 * @author zhicong.lin 2022/1/26
 */
public interface MyJobs {

    /**
     * 任务执行入口
     *
     * @param args 参数
     * @return boolean
     * @author zhicong.lin 2022/1/26
     */
    boolean runner(String args);
}
