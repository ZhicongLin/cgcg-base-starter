package com.cgcg.base.exception;

/**
 * 加密参数错误.
 *
 * @author zhicong.lin
 * @date 2019/6/29
 */
public class EncryptionParamWrongException extends CommonException {
    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     *
     * @param errorCode the error code
     * @param errorMsg  the error msg
     */
    public EncryptionParamWrongException(int errorCode, String errorMsg) {
        super(errorCode, String.format("加密参数错误[%s]", errorMsg));
    }
}
