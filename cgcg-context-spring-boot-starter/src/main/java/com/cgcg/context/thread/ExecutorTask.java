package com.cgcg.context.thread;

import com.cgcg.context.util.UUIDUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public abstract class ExecutorTask implements Runnable {

    private boolean cancel = false;
    private boolean finish = false;
    private Thread currentThread;
    private String id;
    public ExecutorTask() {
        id = UUIDUtils.getUUID();
        log.info("创建任务[{}]", id);
    }
    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        try {
            Thread.sleep(1L);
            this.currentThread = Thread.currentThread();
            if (!cancel) {
                this.call();
                log.info("任务[{}]执行完毕", id);
            }
            this.finish = true;
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 取消任务
     */
    public void cancelTask() {
        if (this.currentThread != null) {
            this.currentThread.interrupt();
        }
        this.cancel = true;
        log.info("任务[{}]取消执行", this.id);
    }

    /**
     * 执行任务
     */
    public abstract void call();
}
