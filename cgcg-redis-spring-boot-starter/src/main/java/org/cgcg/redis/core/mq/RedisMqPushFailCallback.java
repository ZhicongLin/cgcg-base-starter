package org.cgcg.redis.core.mq;

public interface RedisMqPushFailCallback {

    void execute(Message message);
}
