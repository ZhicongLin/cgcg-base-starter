package com.test.base.controller;

import com.cgcg.base.validate.annotation.Validate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * .
 *
 * @author zhicong.lin
 * @date 2019/7/8
 */
@Service
public class TestService {

    @Resource
    JdbcTemplate jdbcTemplate;
    @Validate(value = AuthServiceImpl.class, method = "test")
    public void test(String vld) {
    }

    @PostConstruct
    public void tse() {
        List<Object> query = jdbcTemplate.query("select id, login_name loginName from cl_user order  by id asc", new RowMapper<Object>() {
            @Override
            public Object mapRow(ResultSet resultSet, int i) throws SQLException {
                HashMap<Object, Object> result = new HashMap<>();
                result.put("id", resultSet.getString("id"));
                result.put("loginName", resultSet.getString("loginName"));
                return result;
            }
        });
        for (Object o : query) {
            String s = o.toString();
            System.out.println("s = " + s);
        }
    }
}
