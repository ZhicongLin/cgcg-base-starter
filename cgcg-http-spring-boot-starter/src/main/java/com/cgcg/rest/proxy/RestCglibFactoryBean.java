package com.cgcg.rest.proxy;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

@Setter
@Getter
public class RestCglibFactoryBean implements FactoryBean {

    private Class interfaceClass;

    private Enhancer enhancer = new Enhancer();

    private Object fallbackBean;
    @Override
    public Object getObject() {
        enhancer.setSuperclass(interfaceClass);
        enhancer.setCallback((MethodInterceptor) (proxy, method, args, methodProxy) -> RestBuilderProcessor.invoke(method, args, fallbackBean));
        return enhancer.create();
    }


    @Override
    public Class getObjectType() {
        return this.interfaceClass;
    }

}