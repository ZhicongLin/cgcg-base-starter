package com.cgcg.rest.proxy;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

@Setter
@Getter
public class RestJdkFactoryBean implements FactoryBean {

    private Class interfaceClass;

    @Override
    public Object getObject() {
        return Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, (proxy, method, args) -> RestBuilderProcessor.invoke(method, args));
    }

    @Override
    public Class getObjectType() {
        return this.interfaceClass;
    }

}