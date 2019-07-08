package com.cgcg.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * .
 *
 * @author zhicong.lin
 * @date 2019/7/8
 */
@Slf4j
@Component
public class TestValidate {

    void vali(String key) {
        if (key != null) {
            log.info(key);
        }
    }
}
