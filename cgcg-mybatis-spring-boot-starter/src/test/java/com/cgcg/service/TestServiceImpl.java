package com.cgcg.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cgcg.TestMapper;
import com.cgcg.base.core.exception.CommonException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * .
 * @author zhicong.lin
 * @date 2019/6/20
 */
@Service
public class TestServiceImpl extends ServiceImpl<TestMapper, Third> implements TestService {

    public User save(String name) {
//        TestServiceImpl bean = SpringContextHolder.getBean(TestServiceImpl.class);
//        bean.save2(name);
        User user = new User(name, "222");
//        this.saveOrUpdate(user);
//        if (name.equals("1")) {
//            throw new CommonException("测试事务抱错");
//        }
        return user;
    }

    @Transactional
    public void save2(String name) {
//        User user = new User(name, name);
//        testMapper.insert(user);
        if (name.equals("2")) {
            throw new CommonException("测试事务抱错2");
        }
    }

}
