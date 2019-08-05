package com.cgcg.base.core.exception;

import com.cgcg.base.language.Translator;

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
     * @param parameterStr  the error msg
     */
    public EncryptionParamWrongException(String parameterStr) {
        super(416, String.format(Translator.toLocale("EncryptionParamWrongFmt", "加密参数错误[%s]"), parameterStr));
    }
}
