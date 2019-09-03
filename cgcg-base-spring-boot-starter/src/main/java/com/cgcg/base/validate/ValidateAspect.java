package com.cgcg.base.validate;

import com.cgcg.base.core.exception.CommonException;
import com.cgcg.base.validate.annotation.Validate;
import com.cgcg.context.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * .
 *
 * @author zhicong.lin
 * @date 2019/7/8
 */
@Slf4j
@Aspect
@Component
public class ValidateAspect {

    @Around("@annotation(vldAnn)")
    public Object specification(ProceedingJoinPoint pjp, Validate vldAnn) throws Throwable {
        final Object[] args = pjp.getArgs();
        if (args == null || args.length == 0) { //没有参数，无需对参数进行校验
            return pjp.proceed();
        }
        final MethodSignature signature = (MethodSignature) pjp.getSignature();
        final Method method = signature.getMethod();
        final String specMethod = vldAnn.method();
        final String specMethodName = specMethod.equals("") ?  method.getName() : specMethod;
        try {
            final Class<?>[] parameterTypes = method.getParameterTypes();
            final Class<?> validateClass = vldAnn.value();
            final Object bean = SpringContextHolder.getBean(validateClass);
            final Method validateMethod = validateClass.getDeclaredMethod(specMethodName, parameterTypes);
            validateMethod.setAccessible(true);
            validateMethod.invoke(bean, args); //执行校验方法，校验不通过抛出异常
        } catch (InvocationTargetException e) {
            final Throwable cause = this.getCause(e);
            if (cause instanceof CommonException) {
                throw cause;
            } else {
                log.warn(e.getMessage(), e);
            }
        } catch (NoSuchBeanDefinitionException | IllegalAccessException | NoSuchMethodException e) {
            log.warn(e.getMessage(), e);
        }
        return pjp.proceed();
    }

    private Throwable getCause(Throwable t) {
        final Throwable cause = t.getCause();
        if (cause == null) {
            return t;
        }
        if (cause instanceof CommonException) {
            return cause;
        }
        return getCause(cause);
    }
}
