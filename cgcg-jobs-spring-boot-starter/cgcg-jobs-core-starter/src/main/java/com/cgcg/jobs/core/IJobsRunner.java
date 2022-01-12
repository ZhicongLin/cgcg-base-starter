package com.cgcg.jobs.core;

public interface IJobsRunner {

    JobsRunCallBack invoke(String jobId, String args);
}
