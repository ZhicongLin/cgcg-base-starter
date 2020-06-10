package com.cgcg.base.validate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.cgcg.base.validate.annotation.ParameterValidate;
import com.cgcg.context.SpringContextHolder;
import com.cgcg.context.util.AnnotationUtils;

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
public class ValidateInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        final ParameterValidate validateAnnotation = AnnotationUtils.getAnnotation(methodInvocation, ParameterValidate.class);
        if (validateAnnotation == null) {
            return methodInvocation.proceed();
        }
        final Class<?> clazz = validateAnnotation.value();
        final String methodName = validateAnnotation.method();
        try {
            final String validateMethodName = StringUtils.isBlank(methodName) ? methodInvocation.getMethod().getName() : methodName;
            final Method validateMethod = this.getValidateMethod(clazz, methodName, methodInvocation.getArguments());
            if (validateMethod != null) {
                final Object bean = SpringContextHolder.getBean(clazz);
                validateMethod.invoke(bean, methodInvocation.getArguments());
            }
            return methodInvocation.proceed();
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error(e.getMessage(), e);
            throw new ValidateException(500, "Invoke " + clazz.getName() + "." + methodName + " Exception");
        }
    }

    private Method getValidateMethod(Class<?> clazz, String methodName, Object[] args) {
        try {
            if (args != null && args.length > 0) {
                final Class<?>[] classes = new Class<?>[args.length];
                for (int i = 0; i < args.length; i++) {
                    classes[i] = args[i].getClass();
                }
                final Method declaredMethod = clazz.getDeclaredMethod(methodName, classes);
                declaredMethod.setAccessible(true);
                return declaredMethod;
            }
            final Method declaredMethod = clazz.getDeclaredMethod(methodName);
            declaredMethod.setAccessible(true);
            return declaredMethod;
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

}