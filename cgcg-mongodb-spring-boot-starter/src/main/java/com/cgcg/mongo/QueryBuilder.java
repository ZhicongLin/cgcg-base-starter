package com.cgcg.mongo;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @program: cgcg-base-starter
 * @description: Query 查询条件组装
 * @author: zhicong.lin
 * @create: 2022-02-07 10:33
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QueryBuilder extends MongoDbBuilder {

    private Query query;

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
        query.addCriteria(Criteria.where(name).is(value));
        return this;
    }

    /**
     * 模糊查询
     *
     * @param name
     * @param value
     * @return org.cgcg.mongo.QueryBuilder
     * @author : zhicong.lin
     * @date : 2022/2/7 10:49
     */
    public QueryBuilder like(String name, String value) {
        final Pattern pattern = Pattern.compile("^.*" + value + ".*$");
        query.addCriteria(Criteria.where(name).regex(pattern));
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
        final List<Sort.Order> orders = new ArrayList<>();
        for (String property : fields) {
            orders.add(new Sort.Order(direction, property));
        }
        query.with(Sort.by(orders));
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
        return getTemplate().findOne(this.query, clazz);
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
        return getTemplate().count(this.query, clazz);
    }

    /**
     * 删除数据
     *
     * @param clazz 对象类型
     * @return java.util.List<T>
     * @author : zhicong.lin
     * @date : 2022/2/7 11:50
     */
    public <T> void delete(Class<T> clazz) {
        getTemplate().remove(this.query, clazz);
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
