package org.cgcg.redis.core.mq;

import com.cgcg.context.util.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.cgcg.redis.core.RedisManager;

import java.time.LocalDateTime;

/**
 * Redis消息队列推送工具
 *
 * @author zhicong.lin
 * @date 2022/1/18
 */
@Slf4j
public class RedisMqPublisher {

    /**
     * 推送消息
     *
     * @param channel        通道id
     * @param messageContent 消息内容
     */
    public static void send(String channel, Object messageContent) {
        send(channel, messageContent, 1);
    }

    /**
     * 推送消息
     *
     * @param channel        通道id
     * @param messageContent 消息内容
     * @param retry          重试次数
     */
    public static void send(String channel, Object messageContent, int retry) {
        send(channel, messageContent, retry, message -> {
        });
    }

    /**
     * 推送消息
     *
     * @param channel        通道id
     * @param messageContent 消息内容
     * @param retry          重试次数
     * @param callback       失败回调
     */
    public static void send(String channel, Object messageContent, int retry, RedisMqPushFailCallback callback) {
        for (int i = 0; i < retry; i++) {
            final boolean success = tryPush(channel, messageContent, retry, callback, i);
            if (success) {
                return;
            }
        }
    }

    private static boolean tryPush(String channel, Object messageContent, int retry, RedisMqPushFailCallback callback, int i) {
        final Message message = getMessage(channel, messageContent);
        try {
            RedisManager.getRedisTemplate().convertAndSend(channel, message);
            log.info("Push Message Success [{}]==>{}", channel, message);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (i + 1 < retry) {
                log.error("消息推送失败，1秒后将再进行尝试");
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException interruptedException) {
                    log.error(e.getMessage());
                }
            } else {
                log.error("消息推送失败");
                if (callback != null) {
                    callback.execute(message);
                }
            }
        }
        return false;
    }

    private static Message getMessage(String channel, Object messageContent) {
        final Message message = new Message();
        message.setId(UUIDUtils.getUuid());
        message.setChannel(channel);
        message.setData(messageContent);
        message.setTime(LocalDateTime.now());
        return message;
    }
}
