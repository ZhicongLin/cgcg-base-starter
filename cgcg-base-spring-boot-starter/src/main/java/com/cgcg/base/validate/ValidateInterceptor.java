package com.cgcg.base.validate;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.cgcg.base.core.exception.CommonException;
import com.cgcg.base.validate.annotation.MethodValidate;
import com.cgcg.context.SpringContextHolder;
import com.cgcg.context.util.AnnotationUtils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

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
public class ValidateInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        val validateAnnotation = AnnotationUtils.getAnnotation(methodInvocation, MethodValidate.class);
        if (validateAnnotation == null) {
            return methodInvocation.proceed();
        }
        val clazz = validateAnnotation.value();
        val methodName = validateAnnotation.method();
        try {
            val validateMethod = this.getValidateMethod(clazz, methodName, methodInvocation.getArguments());
            validateMethod.setAccessible(true);
            validateMethod.invoke(SpringContextHolder.getBean(clazz), methodInvocation.getArguments());
        } catch (Exception e) {
            final Throwable cause = this.getCause(e);
            if (cause instanceof CommonException) {
                throw cause;
            }
            log.error(e.getMessage(), e);
            throw new ValidateException(500, "Invoke " + clazz.getName() + "." + methodName + " Exception");
        }
        return methodInvocation.proceed();
    }

    private Throwable getCause(Throwable t) {
        val cause = t.getCause();
        if (cause == null) {
            return t;
        }
        if (cause instanceof CommonException) {
            return cause;
        }
        return getCause(cause);
    }

    @SneakyThrows
    private Method getValidateMethod(Class<?> clazz, String methodName, Object[] args) {
        if (args == null || args.length == 0) {
            return clazz.getDeclaredMethod(methodName);
        }
        val classes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            classes[i] = args[i].getClass();
        }
        return clazz.getDeclaredMethod(methodName, classes);
    }

}