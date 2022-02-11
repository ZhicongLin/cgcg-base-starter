package com.cgcg.base.core.context;

import com.cgcg.base.core.enums.FormatProperty;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author zhicong.lin
 * @date 2018-11-07 22:37
 **/
@Component
public class ApplicationRunnerImpl implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) {
        FormatProperty.init();
    }

}