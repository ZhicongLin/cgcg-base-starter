package org.cgcg.redis.core.annotation;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.cgcg.redis.core.RedisCacheProperties;
import org.cgcg.redis.core.interceptor.RedisCacheInterceptor;
import org.cgcg.redis.core.util.SpringProxyUtils;
import org.springframework.aop.Advisor;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Description: 缓存注解扫描器
 *
 * @author linzc
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本           修改人       修改日期         修改内容
 * 2020/6/5.1       linzc    2020/6/5           Create
 * </pre>
 * @date 2020/6/5
 */
@Setter
@Getter
@Slf4j@Service
public class RedisCacheAnnotationScanner extends AbstractAutoProxyCreator implements ApplicationContextAware, EnvironmentAware {
    private MethodInterceptor interceptor;
    private RedisTemplate<String, Object> redisTemplate;
    private final boolean disable;
    private ApplicationContext applicationContext;
    private Environment environment;

    public RedisCacheAnnotationScanner(RedisCacheProperties redisCacheProperties, RedisTemplate<String, Object> redisTemplate) {
        this.disable = redisCacheProperties.isDisable();
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected Object[] getAdvicesAndAdvisorsForBean(Class<?> aClass, String s, TargetSource targetSource) throws BeansException {
        return new Object[]{interceptor};
    }

    @Override
    protected Object wrapIfNecessary(Object bean, String beanName, Object cacheKey) {
        if (this.disable) {
            return bean;
        }
        try {
            final Class<?> serviceInterface = SpringProxyUtils.findTargetClass(bean);
            final Class<?>[] interfacesIfJdk = SpringProxyUtils.findInterfaces(bean);
            if (!existsAnnotation(serviceInterface) && !existsAnnotation(interfacesIfJdk)) {
                return bean;
            }
            if (interceptor == null) {
                interceptor = new RedisCacheInterceptor(redisTemplate, this.environment);
            }
            log.info("Bean[{}] with name [{}] would use interceptor [{}]", bean.getClass().getName(), beanName, interceptor.getClass().getName());
            if (!AopUtils.isAopProxy(bean)) {
                bean = super.wrapIfNecessary(bean, beanName, cacheKey);
            } else {
                AdvisedSupport advised = SpringProxyUtils.getAdvisedSupport(bean);
                Advisor[] advisor = buildAdvisors(beanName, getAdvicesAndAdvisorsForBean(null, null, null));
                for (Advisor avr : advisor) {
                    advised.addAdvisor(0, avr);
                }
            }
            return bean;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private boolean existsAnnotation(Class<?>... classes) {
        if (classes == null || classes.length == 0) {
            return false;
        }
        for (Class<?> clazz : classes) {
            if (clazz != null) {
                final Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (method.getAnnotation(RedisCache.class) != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.setBeanFactory(applicationContext);
    }

}