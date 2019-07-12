package com.cgcg.rest.exception;

/**
 * 空文件异常.
 *
 * @Author: ZhiCong.Lin
 * @Create: 2018-08-14 14:54
 */
public class NullFileException extends RestException {

    /**
     * 空文件异常
     *
     * @Author: ZhiCong Lin
     */
    public NullFileException() {
        super(100001407, "空文件异常");
    }
}
