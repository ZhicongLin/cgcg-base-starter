package com.cgcg.rest.proxy;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;

@Setter
@Getter
public class RestJdkFactoryBean implements FactoryBean {

    private Class interfaceClass;
    private Object fallbackBean;
    @Override
    public Object getObject() {
        return Proceeding.jdk(interfaceClass, fallbackBean);
    }

    @Override
    public Class getObjectType() {
        return this.interfaceClass;
    }

}