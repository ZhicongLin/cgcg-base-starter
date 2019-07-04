package com.cgcg.base.core.enums;

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
    UTF8("UTF-8");
    private String name;

    public static Charset forUtf8() {
        return Charset.forName(UTF8.getName());
    }
}
