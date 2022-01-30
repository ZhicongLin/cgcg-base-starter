package com.cgcg.rest.proxy;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author zhicong.lin
 */
@Setter
@Getter
public class RestJdkFactoryBean<T> implements FactoryBean<T> {

    private Class<T> interfaceClass;
    private Object fallbackBean;
    @Override
    public T getObject() {
        return Proceeding.jdk(interfaceClass, fallbackBean);
    }

    @Override
    public Class<T> getObjectType() {
        return this.interfaceClass;
    }

}