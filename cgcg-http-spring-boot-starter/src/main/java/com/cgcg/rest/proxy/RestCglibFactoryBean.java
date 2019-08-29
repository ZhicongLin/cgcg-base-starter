package com.cgcg.rest.proxy;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.cglib.proxy.Enhancer;

@Setter
@Getter
public class RestCglibFactoryBean implements FactoryBean {

    private Class interfaceClass;

    private Enhancer enhancer = new Enhancer();

    private Object fallbackBean;
    @Override
    public Object getObject() {
        enhancer.setSuperclass(interfaceClass);
        enhancer.setCallback(Proceeding.cglib(interfaceClass, fallbackBean));
        return enhancer.create();
    }


    @Override
    public Class getObjectType() {
        return this.interfaceClass;
    }

}