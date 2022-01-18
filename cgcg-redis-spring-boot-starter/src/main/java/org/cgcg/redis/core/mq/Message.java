package org.cgcg.redis.core.mq;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
public class Message {

    private String id;

    private String channel;

    private Object data;

    private LocalDateTime time;

}
