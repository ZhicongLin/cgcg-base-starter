package com.cgcg.base.format.encrypt;

import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

/**
 * 接口加密注解.
 *
 * @author zhicong.lin
 * @date 2019/6/29
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RestController
@Encrypt
public @interface EncryptController {
}
