package com.cgcg.mongo;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.lang.reflect.Field;

/**
 * @program: cgcg-base-starter
 * @description: 修改构建器
 * @author: zhicong.lin
 * @create: 2022-02-07 11:17
 **/
@Slf4j
public class UpdateBuilder extends MongoDbBuilder {

    private Update update;

    private Class<?> clazz;

    /**
     * 创建构建器
     *
     * @return org.cgcg.mongo.QueryBuilder
     * @author : zhicong.lin
     * @date : 2022/2/7 10:41
     */
    public static <T> UpdateBuilder builder(Class<T> clazz) {
        UpdateBuilder builder = new UpdateBuilder();
        builder.update = new Update();
        builder.clazz = clazz;
        return builder;
    }

    private <T> void setObject(T bean) {
        final Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(bean);
                this.update.set(field.getName(), value);
            } catch (IllegalAccessException e) {
                log.warn(e.getMessage());
            }
        }
    }

    /**
     * 设置修改的字段
     *
     * @param field
     * @param value
     * @return org.cgcg.mongo.UpdateBuilder
     * @author : zhicong.lin
     * @date : 2022/2/7 13:50
     */
    public UpdateBuilder set(String field, Object value) {
        this.update.set(field, value);
        return this;
    }

    /**
     * 根据id修改
     *
     * @param id
     * @return void
     * @author : zhicong.lin
     * @date : 2022/2/7 13:50
     */
    public void updateById(Object id) {
        updateByName(ID, id);
    }

    /**
     * 保存或者修改
     *
     * @param bean
     * @return void
     * @author : zhicong.lin
     * @date : 2022/2/7 13:49
     */
    public <T> void saveOrUpdate(T bean) {
        Object id;
        try {
            final Field idField = clazz.getDeclaredField(ID);
            idField.setAccessible(true);
            id = idField.get(bean);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.warn(e.getMessage());
            return;
        }
        if (id == null) {
            getTemplate().save(bean);
            log.debug("Save [{}]: {}", clazz, JSON.toJSONString(bean));
        } else {
            final Query query = QueryBuilder.builder().id(id).build();
            setObject(bean);
            getTemplate().updateFirst(query, update, clazz);
            log.debug("Modify [{}]: {}", clazz, JSON.toJSONString(update.getUpdateObject()));
        }
    }

    /**
     * 根据字段name修改
     *
     * @param name
     * @param value
     * @return void
     * @author : zhicong.lin
     * @date : 2022/2/7 13:49
     */
    public void updateByName(String name, Object value) {
        final Query query = QueryBuilder.builder().eq(name, value).build();
        final UpdateResult result = getTemplate().updateFirst(query, update, clazz);
        log.debug("Modify [{}]: {}", clazz, JSON.toJSONString(update.getUpdateObject()));
    }

    /**
     * 删除
     *
     * @param id
     * @return void
     * @author : zhicong.lin
     * @date : 2022/2/7 13:49
     */
    public void deleteById(Object id) {
        final Query query = QueryBuilder.builder().id(id).build();
        getTemplate().remove(query, clazz);
        log.debug("Delete [{}]: id={}", clazz, id);
    }

    /**
     * 删除
     *
     * @param bean
     * @return void
     * @author : zhicong.lin
     * @date : 2022/2/7 13:49
     */
    public <T> void delete(T bean) {
        getTemplate().remove(bean);
        log.debug("Delete [{}]: {}", clazz, JSON.toJSONString(bean));
    }

    /**
     * 创建update
     *
     * @return org.springframework.data.mongodb.core.query.Query
     * @author : zhicong.lin
     * @date : 2022/2/7 13:48
     */
    public Update build() {
        return this.update;
    }

}
