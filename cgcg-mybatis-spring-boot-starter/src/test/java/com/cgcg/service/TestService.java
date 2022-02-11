package com.cgcg.service;

import com.cgcg.TestMapper;
import com.cgcg.base.core.exception.CommonException;
import com.cgcg.context.SpringContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * .
 *
 * @author zhicong.lin
 * @date 2019/6/20
 */
@Service
public class TestService {


    @Resource
    TestMapper testMapper;

    public Object findAll() {
        return this.testMapper.findAll();
    }

    public User save(String name) {
        TestService bean = SpringContextHolder.getBean(TestService.class);
        bean.save2(name);
        User user = new User(name, "222");
        testMapper.insert(user);
        if (name.equals("1")) {
            throw new CommonException("测试事务抱错");
        }
        return user;
    }

    @Transactional
    public void save2(String name) {
        User user = new User(name, name);
        testMapper.insert(user);
        if (name.equals("2")) {
            throw new CommonException("测试事务抱错2");
        }
    }
}
