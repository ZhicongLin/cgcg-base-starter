package org.cgcg.redis.core.mq;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhicong.lin
 */
@Setter
@Getter
@ToString
public class Message implements Serializable {

    private String id;

    private String channel;

    private Object data;

    private Date time;

}
