package com.cgcg.rest.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * RestClient异常.
 *
 * @Author: ZhiCong.Lin
 * @Create: 2018-08-09 10:48
 */
@Setter
@Getter
@NoArgsConstructor
public class RestException extends RuntimeException {

    private int errorCode;

    private String errorMsg;

    public RestException(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    @Override
    public String getMessage() {
        return errorMsg;
    }
}