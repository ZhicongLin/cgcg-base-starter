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

    @Override
    public Object getObject() throws Exception {
        enhancer.setSuperclass(interfaceClass);
        enhancer.setCallback((MethodInterceptor) RestBuilderProcessor::intercept);
        return enhancer.create();
    }


    @Override
    public Class getObjectType() {
        return this.interfaceClass;
    }

}