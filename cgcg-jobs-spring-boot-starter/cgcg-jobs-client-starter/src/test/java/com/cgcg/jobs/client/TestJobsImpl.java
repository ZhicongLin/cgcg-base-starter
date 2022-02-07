package com.cgcg.jobs.client;

import com.cgcg.jobs.client.annotation.Jobs;
import com.cgcg.jobs.core.MyJobs;

@Jobs("jobs-testKey")
public class TestJobsImpl implements MyJobs {
    @Override
    public boolean runner(String args) {
        System.out.println("args = " + args);
        return false;
    }
}
