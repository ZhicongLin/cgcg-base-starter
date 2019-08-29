package com.cgcg.rest.proxy;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

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

    public Object proceed() throws Exception {
        return proceed(this.arguments);
    }

    public Object proceed(Object[] arguments) throws Exception {
        return method.invoke(this.instance, arguments);
    }

    public static MethodInterceptor cglib(Class interfaceClass, Object fallbackBean) {
        return (o, method, arguments, methodProxy) -> RestBuilderProcessor.invoke(new Proceeding(method, arguments, interfaceClass, fallbackBean));
    }

    public static InvocationHandler jdk(Class interfaceClass, Object fallbackBean) {
        return (proxy, method, args) -> RestBuilderProcessor.invoke(new Proceeding(method, args, interfaceClass, fallbackBean));
    }
}
