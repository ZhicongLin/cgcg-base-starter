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

    /**
     * 获取字符编码类型
     *
     * @return java.nio.charset.Charset
     * @author zhicong.lin
     * @date 2022/2/8 9:56
     */
    public static Charset forUtf8() {
        return Charset.forName(UTF8.getCharset());
    }
}
