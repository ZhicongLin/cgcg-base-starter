package com.cgcg.base.validate;

import java.util.Arrays;

import javax.annotation.Resource;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.cgcg.base.validate.annotation.ParameterValidate;
import com.cgcg.context.util.AnnotationUtils;
import com.cgcg.context.util.SpringProxyUtils;

import lombok.extern.slf4j.Slf4j;

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
    @Resource
    @Qualifier("validateInterceptor")
    private MethodInterceptor interceptor;

    @Override
    protected Object[] getAdvicesAndAdvisorsForBean(Class<?> aClass, String s, TargetSource targetSource) throws BeansException {
        return new Object[]{interceptor};
    }


    @Override
    protected Object wrapIfNecessary(Object bean, String beanName, Object cacheKey) {
        if (!AnnotationUtils.existMethodAnn(bean, ParameterValidate.class)) {
            return bean;
        }
        if (interceptor == null) {
            interceptor = new ValidateInterceptor();
        }
        log.info("Bean[{}] with name [{}] would use interceptor [{}]", bean.getClass().getName(), beanName, interceptor.getClass().getName());
        try {
            if (!AopUtils.isAopProxy(bean)) {
                bean = super.wrapIfNecessary(bean, beanName, cacheKey);
            } else {
                final AdvisedSupport advised = SpringProxyUtils.getAdvisedSupport(bean);
                final Advisor[] advisor = buildAdvisors(beanName, getAdvicesAndAdvisorsForBean(Void.TYPE, beanName, null));
                Arrays.stream(advisor).forEach(avr -> advised.addAdvisor(0, avr));
            }
            return bean;
        } catch (Exception e) {
            logger.error(e);
            throw new ValidateException(500, "Spring Proxy [" + beanName + "] Exception");
        }
    }
}