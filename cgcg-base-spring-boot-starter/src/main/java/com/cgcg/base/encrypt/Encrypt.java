package com.cgcg.base.encrypt;

import java.lang.annotation.*;

/**
 * 接口加密注解.
 *
 * @author zhicong.lin
 * @date 2019/6/29
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Encrypt {
}
