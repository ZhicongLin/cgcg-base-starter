package com.cgcg.base.validate;

import com.cgcg.base.core.exception.CommonException;

/**
 * 数据校验异常.
 *
 * @author zhicong.lin
 * @date 2019/7/8
 */
public class ValidateException extends CommonException {
    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     *
     * @param errorCode the error code
     * @param errorMsg  the error msg
     */
    public ValidateException(int errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }
}
