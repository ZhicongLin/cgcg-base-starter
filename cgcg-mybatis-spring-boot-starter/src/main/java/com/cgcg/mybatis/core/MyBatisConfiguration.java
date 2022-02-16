package com.cgcg.mybatis.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.optimize.JsqlParserCountOptimize;

/**
 * 配置
 *
 * @author zhicong.lin
 * @date 2022-02-16 14:56
 **/
@Configuration
public class MyBatisConfiguration {

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        final PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        paginationInterceptor.setCountSqlParser(new JsqlParserCountOptimize(true));
        return paginationInterceptor;
    }
}
