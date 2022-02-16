package com.cgcg.mongo.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @program: cgcg-base-starter
 * @description: Query 查询条件组装
 * @author: zhicong.lin
 * @create: 2022-02-07 10:33
 **/
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QueryBuilder extends MongoDbBuilder {
    private Query query;
    private Pageable pageable;
    private Criteria criteria;
    private final List<Criteria> orCriteria = new ArrayList<>();

    /**
     * 创建构建器
     *
     * @return org.cgcg.mongo.QueryBuilder
     * @author : zhicong.lin
     * @date : 2022/2/7 10:41
     */
    public static QueryBuilder builder() {
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.query = new Query();
        return queryBuilder;
    }

    /**
     * 全字段匹配
     *
     * @param name
     * @param value
     * @return org.cgcg.mongo.QueryBuilder
     * @author : zhicong.lin
     * @date : 2022/2/7 10:49
     */
    public QueryBuilder eq(String name, Object value) {
        if (criteria == null) {
            criteria = Criteria.where(name).is(value);
            query.addCriteria(criteria);
        } else {
            criteria.and(name).is(value);
        }
        return this;
    }

    /**
     * 全字段匹配 IS NULL
     *
     * @param name
     * @return org.cgcg.mongo.QueryBuilder
     * @author : zhicong.lin
     * @date : 2022/2/7 10:49
     */
    public QueryBuilder isNull(String name) {
        if (criteria == null) {
            criteria = Criteria.where(name).isNull();
            query.addCriteria(criteria);
        } else {
            criteria.and(name).isNull();
        }
        return this;
    }

    /**
     * 全字段匹配 IS NOT NULL
     *
     * @param name
     * @return org.cgcg.mongo.QueryBuilder
     * @author : zhicong.lin
     * @date : 2022/2/7 10:49
     */
    public QueryBuilder isNotNull(String name) {
        if (criteria == null) {
            criteria = Criteria.where(name).exists(true);
            query.addCriteria(criteria);
        } else {
            criteria.and(name).exists(true);
        }
        return this;
    }

    /**
     * ID字段匹配
     *
     * @param id
     * @return org.cgcg.mongo.QueryBuilder
     * @author : zhicong.lin
     * @date : 2022/2/7 10:49
     */
    public QueryBuilder id(Object id) {
        eq(ID, id);
        return this;
    }

    /**
     * 模糊查询
     *
     * @param name
     * @param value
     * @return org.cgcg.mongo.QueryBuilder
     * @author zhicong.lin
     * @date : 2022/2/7 10:49
     */
    public QueryBuilder like(String name, String value) {
        final Pattern pattern = Pattern.compile("^.*" + value + ".*$");
        if (criteria == null) {
            criteria = Criteria.where(name).regex(pattern);
            query.addCriteria(criteria);
        } else {
            criteria.and(name).regex(pattern);
        }
        return this;
    }

    /**
     * in
     *
     * @param name
     * @param values
     * @return org.cgcg.mongo.QueryBuilder
     * @author zhicong.lin
     * @date : 2022/2/7 10:49
     */
    public QueryBuilder in(String name, Object... values) {
        if (criteria == null) {
            criteria = Criteria.where(name).in(values);
            query.addCriteria(criteria);
        } else {
            criteria.and(name).in(values);
        }
        return this;
    }

    /**
     * or In
     *
     * @param name
     * @param values
     * @return org.cgcg.mongo.QueryBuilder
     * @author zhicong.lin
     * @date : 2022/2/7 10:49
     */
    public QueryBuilder orIn(String name, Object... values) {
        orCriteria.add(Criteria.where(name).in(values));
        return this;
    }

    /**
     * or eq
     *
     * @param name
     * @param values
     * @return org.cgcg.mongo.QueryBuilder
     * @author zhicong.lin
     * @date : 2022/2/7 10:49
     */
    public QueryBuilder orEq(String name, Object values) {
        if (values == null) {
            orCriteria.add(Criteria.where(name).isNull());
        } else {
            orCriteria.add(Criteria.where(name).is(values));
        }
        return this;
    }

    /**
     * or eq
     *
     * @param name
     * @return org.cgcg.mongo.QueryBuilder
     * @author zhicong.lin
     * @date : 2022/2/7 10:49
     */
    public QueryBuilder orExists(String name) {
        orCriteria.add(Criteria.where(name).exists(true));
        return this;
    }

    /**
     * or eq
     *
     * @param name
     * @param value
     * @return org.cgcg.mongo.QueryBuilder
     * @author zhicong.lin
     * @date : 2022/2/7 10:49
     */
    public QueryBuilder orLike(String name, Object value) {
        final Pattern pattern = Pattern.compile("^.*" + value + ".*$");
        orCriteria.add(Criteria.where(name).regex(pattern));
        return this;
    }

    /**
     * 分页参数
     *
     * @param pageNum
     * @param pageSize
     * @return com.cgcg.mongo.QueryBuilder
     * @author : zhicong.lin
     * @date : 2022/2/7 17:39
     */
    public QueryBuilder pageable(int pageNum, int pageSize) {
        final int pageNumber = pageNum - 1;
        this.pageable = Pageable.ofSize(pageSize).withPage(Math.max(pageNumber, 0));
        this.query.with(pageable);
        return this;
    }

    /**
     * 正序排列
     *
     * @param fields 字段名
     * @return org.cgcg.mongo.QueryBuilder
     * @author : zhicong.lin
     * @date : 2022/2/7 10:49
     */
    public QueryBuilder asc(String... fields) {
        return this.sort(Sort.Direction.ASC, fields);
    }

    /**
     * 倒序排列
     *
     * @param fields 字段名
     * @return org.cgcg.mongo.QueryBuilder
     * @author : zhicong.lin
     * @date : 2022/2/7 10:50
     */
    public QueryBuilder desc(String... fields) {
        return this.sort(Sort.Direction.DESC, fields);
    }

    /**
     * 排序
     *
     * @param direction 排序方式
     * @param fields    字段名
     * @return org.cgcg.mongo.QueryBuilder
     * @author : zhicong.lin
     * @date : 2022/2/7 10:51
     */
    public QueryBuilder sort(Sort.Direction direction, String... fields) {
        final List<Sort.Order> orders = Arrays.stream(fields)
                .map(field -> new Sort.Order(direction, field))
                .collect(Collectors.toList());
        this.query.with(Sort.by(orders));
        return this;
    }

    /**
     * 获取单个对象
     *
     * @param clazz 对象类型
     * @return T
     * @author : zhicong.lin
     * @date : 2022/2/7 11:49
     */
    public <T> T findOne(Class<T> clazz) {
        if (!orCriteria.isEmpty()) {
            criteria.orOperator(orCriteria);
        }
        return getTemplate().findOne(this.query, clazz);
    }

    /**
     * 获取单个对象
     *
     * @param clazz 对象类型
     * @return T
     * @author : zhicong.lin
     * @date : 2022/2/7 11:49
     */
    public <T> T findById(Object id, Class<T> clazz) {
        this.id(id);
        return findOne(clazz);
    }

    /**
     * 获取对象列表数据
     *
     * @param clazz 对象类型
     * @return java.util.List<T>
     * @author : zhicong.lin
     * @date : 2022/2/7 11:50
     */
    public <T> List<T> find(Class<T> clazz) {
        if (!orCriteria.isEmpty()) {
            criteria.orOperator(orCriteria);
        }
        return getTemplate().find(this.query, clazz);
    }

    /**
     * 获取对象列表数量
     *
     * @param clazz 对象类型
     * @return java.util.List<T>
     * @author : zhicong.lin
     * @date : 2022/2/7 11:50
     */
    public <T> long count(Class<T> clazz) {
        if (!orCriteria.isEmpty()) {
            criteria.orOperator(orCriteria);
        }
        return getTemplate().count(this.query, clazz);
    }

    /**
     * 获取分页数据
     *
     * @param clazz
     * @return org.springframework.data.domain.Page<T>
     * @author : zhicong.lin
     * @date : 2022/2/8 9:00
     */
    public <T> Page<T> findPage(Class<T> clazz) {
        if (!orCriteria.isEmpty()) {
            criteria.orOperator(orCriteria);
        }
        return new PageImpl<>(find(clazz), pageable, count(clazz));
    }

    /**
     * 创建query
     *
     * @return org.springframework.data.mongodb.core.query.Query
     * @author : zhicong.lin
     * @date : 2022/2/7 13:48
     */
    public Query build() {
        return this.query;
    }

}
