package com.cgcg.service;

import org.cgcg.redis.core.annotation.Rmqp;
import org.cgcg.redis.core.annotation.Rmqc;
import org.cgcg.redis.core.annotation.RmqListener;
import org.cgcg.redis.core.mq.Message;

import java.util.Map;

@RmqListener
public class RedisMQtest {

    @Rmqc(value = "Redis::MQ1")
    public void test(Message value, String channel) {
        System.out.println("收到消息1 = " + value);
    }

    @Rmqc(value = "Redis::MQ2")
    public void test2(Message value) {
        System.out.println("收到消息2 = " + value);
    }

    @Rmqp(value = {"Redis::MQ1", "Redis::MQ2"})
    public Map<String, Object> producer(Map<String, Object> value) {
        return value;
    }

}
