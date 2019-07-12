package com.cgcg.rest.exception;

/**
 * 找不到RestTemplate工具类.
 *
 * @Author: ZhiCong.Lin
 * @Create: 2018-08-14 09:34
 */
public class NoFoundRestTemplateException extends RestException {

    /**
     * 找不到RestTemp[]  * @Param: [errorCode, errorMsg]
     *
     * @Return:
     * @Author: ZhiCong Lin
     * @Date:
     */
    public NoFoundRestTemplateException() {
        super(100001405, "找不到RestTemplateFactory");
    }

}
