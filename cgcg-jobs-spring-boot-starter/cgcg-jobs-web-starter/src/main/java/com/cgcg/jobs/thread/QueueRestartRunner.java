package com.cgcg.jobs.thread;

import com.cgcg.jobs.model.TaskInfo;
import com.cgcg.jobs.quartz.SchedulerQuartzJob;
import com.cgcg.jobs.quartz.SchedulerQuartzService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.LinkedBlockingQueue;
/**
 * @author zhicong.lin
 */
@Slf4j
@Component
public class QueueRestartRunner implements Runnable {

    private final LinkedBlockingQueue<TaskInfo> restartQueue = new LinkedBlockingQueue<>();
    @Resource
    private SchedulerQuartzService schedulerQuartzService;

    @Override
    public void run() {
        while (true) {
            try {
                final TaskInfo info = restartQueue.take();
                restart(info);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void restart(TaskInfo info) {
        try {
            if (SchedulerQuartzJob.runningKey.contains(info.getId())) {
                log.warn("上一次任务正在执行，等候执行完成后重启！");
                restartQueue.put(info);
            } else {
                restartQueue.remove(info);
                schedulerQuartzService.resumeJob(info.getTaskKey(), info.getGroupKey(), info);
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
            this.restartQueue.put(info);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
