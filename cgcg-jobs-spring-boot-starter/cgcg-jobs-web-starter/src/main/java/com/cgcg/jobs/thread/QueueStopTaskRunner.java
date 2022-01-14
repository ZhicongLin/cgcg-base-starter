package com.cgcg.jobs.thread;

import com.cgcg.jobs.model.TaskInfo;
import com.cgcg.jobs.quartz.SchedulerQuartzJob;
import com.cgcg.jobs.quartz.SchedulerQuartzService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Component
public class QueueStopTaskRunner implements Runnable {

    private final LinkedBlockingQueue<TaskInfo> stopQueue = new LinkedBlockingQueue<>();

    @Resource
    private SchedulerQuartzService schedulerQuartzService;

    @Override
    public void run() {
        while (true) {
            try {
                final TaskInfo info = stopQueue.take();
                stopTask(info);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopTask(TaskInfo info) {
        try {
            if (SchedulerQuartzJob.runningKey.contains(info.getId())) {
                log.warn("上一次任务正在执行，等候执行完成后再进行停止操作！");
                stopQueue.put(info);
            } else {
                stopQueue.remove(info);
                schedulerQuartzService.deleteJob(info.getTaskKey(), info.getGroupKey());
            }

        } catch (InterruptedException | SchedulerException e) {
            log.error(e.getMessage());
        } finally {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void push(TaskInfo info) {
        try {
            this.stopQueue.put(info);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
