package com.cgcg.context.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.charset.Charset;

/**
 * 字符编码.
 *
 * @author zhicong.lin
 * @date 2019/6/25
 */
@Getter
@AllArgsConstructor
public enum CharsetCode {

    /**
     * UTF-8
     */
    UTF8("UTF-8");
    private final String charset;

    public static Charset forUtf8() {
        return Charset.forName(UTF8.getCharset());
    }
}
