package com.cgcg.rest.proxy;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.cglib.proxy.Enhancer;

/**
 * @author zhicong.lin
 */
@Setter
@Getter
public class RestCglibFactoryBean<T> implements FactoryBean<T> {

    private Class<T> interfaceClass;

    private Enhancer enhancer = new Enhancer();

    private Object fallbackBean;

    @Override
    public T getObject() {
        return Proceeding.cglib(interfaceClass, fallbackBean, enhancer);
    }

    @Override
    public Class<T> getObjectType() {
        return this.interfaceClass;
    }

}