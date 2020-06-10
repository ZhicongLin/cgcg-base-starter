package com.cgcg.rest.exception;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 异常对象.
 *
 * @Author: ZhiCong.Lin
 * @Create: 2018-08-13 09:15
 */
@Slf4j
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorFactory {

    private Integer errorCode;

    private String errorMsg;

    /**
     * 获取异常结果 .
     *
     * @Param: [errorBody]
     * @Author: ZhiCong Lin
     * @Date: 2018/8/13 9:30
     */
    public static ErrorFactory getError(String errorBody) {
        try {
            JSONObject object = JSON.parseObject(errorBody);
            String error = object.getString("error");
            if ("unauthorized".equals(error)) {
                return new ErrorFactory(100001401, "无权访问该接口");
            } else if ("invalid_token".equals(error)) {
                return new ErrorFactory(100002401, "授权码无效");
            }
        } catch (JSONException je) {
            log.error(je.getMessage(), je);
        }
        return null;
    }

    public static void main(String[] args) {
        ErrorFactory error = getError("{1234");
    }
}
