package org.cgcg.redis.core.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

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

    public Map<String, Object> getDataMap() {
        String dataJson = JSON.toJSONString(data);
        return JSONObject.parseObject(dataJson);
    }
}
