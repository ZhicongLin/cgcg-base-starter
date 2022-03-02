package com.cgcg.service;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * .
 * @author zhicong.lin
 * @date 2019/6/20
 */
public interface TestService extends IService<Third> {


    public User save(String name);

    public void save2(String name);

}
