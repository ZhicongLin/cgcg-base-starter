package org.cgcg.redis.core.mq;

/**
 * @author zhicong.lin
 */
public interface RedisMqPushFailCallback {

    /**
     * 消息队列执行失败回调
     *
     * @param message 消息内容
     * @return void
     * @author zhicong.lin 2022/1/26
     */
    void execute(Message message);
}
