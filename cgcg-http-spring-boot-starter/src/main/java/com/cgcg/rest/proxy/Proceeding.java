package com.cgcg.rest.proxy;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Setter
@Getter
public class Proceeding {
    private Method method;
    private Object[] arguments;
    private Class<?> target;
    private Object instance;
    private Logger logger;
    private String logName;

    public Proceeding(Method method, Object[] arguments, Class<?> target, Object instance) {
        this.method = method;
        this.arguments = arguments;
        this.target = target;
        this.instance = instance;
        this.logName = target.getName() + "#" + method.getName();
        this.logger = LoggerFactory.getLogger(this.logName);
    }

    public static Object cglib(Class interfaceClass, Object fallbackBean, Enhancer enhancer) {
        final MethodInterceptor methodInterceptor = (o, method, arguments, methodProxy) -> RestBuilderProcessor.invoke(new Proceeding(method, arguments, interfaceClass, fallbackBean));
        enhancer.setSuperclass(interfaceClass);
        enhancer.setCallback(methodInterceptor);
        return enhancer.create();
    }

    public static Object jdk(Class interfaceClass, Object fallbackBean) {
        final InvocationHandler invocationHandler = (proxy, method, args) -> RestBuilderProcessor.invoke(new Proceeding(method, args, interfaceClass, fallbackBean));
        return Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, invocationHandler);
    }
}
