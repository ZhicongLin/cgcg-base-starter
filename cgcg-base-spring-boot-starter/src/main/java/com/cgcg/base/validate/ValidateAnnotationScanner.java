package com.cgcg.base.validate;

import java.util.Arrays;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Component;

import com.cgcg.base.validate.annotation.MethodValidate;
import com.cgcg.context.util.AnnotationUtils;
import com.cgcg.context.util.SpringProxyUtils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.var;

/**
 * Description:
 *
 * @author linzc
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本           修改人       修改日期         修改内容
 * 2020/6/10.1       linzc    2020/6/10           Create
 * </pre>
 * @date 2020/6/10
 */
@Slf4j
@Component
public class ValidateAnnotationScanner extends AbstractAutoProxyCreator {
    private MethodInterceptor interceptor;

    @Override
    protected Object[] getAdvicesAndAdvisorsForBean(Class<?> aClass, String s, TargetSource targetSource) throws BeansException {
        return new Object[]{interceptor};
    }


    @Override
    @SneakyThrows
    protected Object wrapIfNecessary(Object bean, String beanName, Object cacheKey) {
        if (!AnnotationUtils.existMethodAnn(bean, MethodValidate.class)) {
            return bean;
        }
        if (interceptor == null) {
            interceptor = new ValidateInterceptor();
        }
        log.info("Bean[{}] with name [{}] would use interceptor [{}]", bean.getClass().getName(), beanName, interceptor.getClass().getName());
        if (!AopUtils.isAopProxy(bean)) {
            bean = super.wrapIfNecessary(bean, beanName, cacheKey);
        } else {
            var advised = SpringProxyUtils.getAdvisedSupport(bean);
            var advisor = buildAdvisors(beanName, getAdvicesAndAdvisorsForBean(Void.TYPE, beanName, null));
            Arrays.stream(advisor).forEach(avr -> advised.addAdvisor(0, avr));
        }
        return bean;
    }
}